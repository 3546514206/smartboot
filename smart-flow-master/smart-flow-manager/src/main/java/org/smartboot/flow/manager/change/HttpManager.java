package org.smartboot.flow.manager.change;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.attribute.AttributeHolder;
import org.smartboot.flow.core.attribute.Attributes;
import org.smartboot.flow.core.manager.EngineManager;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.manager.HostUtils;
import org.smartboot.flow.manager.ManagerConstants;
import org.smartboot.flow.manager.NamedThreadFactory;
import org.smartboot.flow.manager.reload.Reloader;
import org.smartboot.http.client.HttpClient;
import org.smartboot.http.client.HttpPost;
import org.smartboot.http.common.enums.HeaderNameEnum;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author qinluo
 * @date 2022-11-25 22:12:24
 * @since 1.0.0
 */
public class HttpManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpManager.class);
    private final ScheduledExecutorService executorService
            = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("smart-flow-manager-thread"));

    private String url;
    private long timeout;
    private Map<String, String> headers;
    private long idle = 5000L;
    private HttpClient client;
    private String path;
    private long delayAtFirst;
    private long lastest;
    private Reloader reloader;

    public void start() {
        URL parsedUrl;
        try {
            parsedUrl = new URL(url);
        } catch (Exception e) {
            throw new IllegalStateException("invalid url " + url, e);
        }

        path = parsedUrl.getPath();
        if (parsedUrl.getQuery() != null) {
            path = path + "?" + parsedUrl.getQuery();
        }

        client = new HttpClient(parsedUrl.getHost(), parsedUrl.getPort());
        client.configuration().connectTimeout((int) timeout);

        lastest = System.currentTimeMillis();
        executorService.schedule(this::pull, delayAtFirst, TimeUnit.MILLISECONDS);
    }

    public void pull() {
        HttpPost post = client.post(path);

        if (headers != null) {
            headers.forEach((key, value) -> post.header().add(key, value));
        }

        long timestamp = lastest;

        try {
            RequestModel model = new RequestModel();
            model.setAddress(HostUtils.getHostIp());
            model.setHost(HostUtils.getHostName());
            model.setTimestamp(timestamp);
            // 只请求当前机器有的engines
            model.setEngineNames(EngineManager.defaultManager().getRegisteredEngineNames());


            String json = JSON.toJSONString(model);
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

            post.header().add(HeaderNameEnum.CONTENT_TYPE.getName(), "application/json;charset=UTF-8").add(HeaderNameEnum.CONTENT_LENGTH.getName(), String.valueOf(bytes.length))
                    .done().body().write(bytes, 0, bytes.length).done().onSuccess(httpResponse -> {
                        if (httpResponse.getStatus() != ManagerConstants.SUCCESS) {
                            LOGGER.info("request remote address failed {}, code = {}", url, httpResponse.getStatus());
                            return;
                        }

                        LOGGER.info("request remote address success {}", url);
                        String body = httpResponse.body();
                        List<ChangeModel> changeModels = JSON.parseArray(body, ChangeModel.class);

                        for (ChangeModel cm : changeModels) {
                            if (cm.getTimestamp() < timestamp) {
                                continue;
                            }

                            ManagerAction action = ManagerAction.get(cm.getAction());
                            if (action == null) {
                                LOGGER.error("unknown action {}", cm.getAction());
                                continue;
                            }

                            if (action == ManagerAction.CHANGE_ATTRIBUTES) {
                                AssertUtil.notBlank(cm.getIdentifier(), "identifier must not be null");
                                AssertUtil.notBlank(cm.getValue(), "value must not be null");
                                Attributes attribute = Attributes.byName(cm.getName());
                                if (attribute == null) {
                                    LOGGER.error("unknown supported attribute {}, please check version", cm.getName());
                                    continue;
                                }

                                try {
                                    EngineManager.defaultManager().changeAttributes(cm.getIdentifier(), AttributeHolder.of(attribute, cm.getValue()));
                                } catch (Exception e) {
                                    LOGGER.error("update attribute failed, attribute = {}, identifier = {}, value = {}",
                                            attribute, cm.getIdentifier(), cm.getValue(), e);
                                }
                            } else if (action == ManagerAction.RESET_METRICS) {
                                EngineManager.defaultManager().resetStatistic(cm.getIdentifier());
                            } else if (action == ManagerAction.RELOAD) {
                                if (reloader == null) {
                                    LOGGER.error("reloader is null, engine = {}", cm.getName());
                                    continue;
                                }
                                // reload.
                                reloader.reload(cm.getName());
                            }

                        }
                        lastest = System.currentTimeMillis();

                    })
                    .onFailure(throwable -> LOGGER.error("request remote address {} failed", url, throwable));

        } catch (Exception e) {
            LOGGER.error("request remote address {} failed", url, e);
        } finally {
            this.executorService.schedule(this::pull, idle, TimeUnit.MILLISECONDS);
        }
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public long getIdle() {
        return idle;
    }

    public void setIdle(long idle) {
        AssertUtil.isTrue(idle > 0, "idle must great than zero");
        this.idle = idle;
    }

    public long getDelayAtFirst() {
        return delayAtFirst;
    }

    public void setDelayAtFirst(long delayAtFirst) {
        this.delayAtFirst = delayAtFirst;
    }

    public Reloader getReloader() {
        return reloader;
    }

    public void setReloader(Reloader reloader) {
        this.reloader = reloader;
    }
}
