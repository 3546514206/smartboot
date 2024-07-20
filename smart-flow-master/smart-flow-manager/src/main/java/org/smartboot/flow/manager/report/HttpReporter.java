package org.smartboot.flow.manager.report;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.ExecutionListenerRegistry;
import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.core.Measurable;
import org.smartboot.flow.core.Pipeline;
import org.smartboot.flow.core.component.Component;
import org.smartboot.flow.core.manager.EngineModel;
import org.smartboot.flow.core.metrics.DefaultMetricsCreator;
import org.smartboot.flow.core.metrics.MetricsCreator;
import org.smartboot.flow.core.metrics.MetricsManager;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.manager.FlatEngine;
import org.smartboot.flow.manager.FlatManager;
import org.smartboot.flow.manager.HostUtils;
import org.smartboot.flow.manager.ManagerConstants;
import org.smartboot.flow.manager.UpdateContentTask;
import org.smartboot.flow.manager.metric.DefaultMetrics;
import org.smartboot.flow.manager.metric.MetricExecutionListener;
import org.smartboot.http.client.HttpClient;
import org.smartboot.http.client.HttpPost;
import org.smartboot.http.common.enums.HeaderNameEnum;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author qinluo
 * @date 2022/11/23 20:47
 * @since 1.0.0
 */
public class HttpReporter extends AbstractReporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpReporter.class);
    private static final SerializeConfig SC = new SerializeConfig();

    static {
        SC.put(DefaultMetrics.class, (jsonSerializer, o, o1, type, i) -> jsonSerializer.write(o.toString()));
    }

    /**
     * 服务端地址
     */
    private String serverAddress;

    /**
     * 超时时间
     */
    private long timeout;
    private String host;
    private int port;

    private Map<String, String> headers;

    @Override
    public void start() {
        URL parsedUrl;
        try {
            parsedUrl = new URL(serverAddress);
        } catch (Exception e) {
            throw new IllegalStateException("invalid url " + serverAddress, e);
        }
        this.host = parsedUrl.getHost();
        this.port = parsedUrl.getPort();

        // Register creator and listener.
        MetricsCreator metricsCreator = MetricsManager.getMetricsCreator();
        if (metricsCreator == DefaultMetricsCreator.INSTANCE) {
            ExecutionListenerRegistry.register(MetricExecutionListener.getInstance());
        }

        UpdateContentTask.startTask(host, port);
        super.start();
    }

    @Override
    public void doExport(EngineModel model) {
        AssertUtil.notNull(host, "address is invalid.");

        HttpClient httpClient = new HttpClient(host, port);
        httpClient.configuration().connectTimeout((int) timeout);
        HttpPost post = httpClient.post(ManagerConstants.REPORT_METRICS);

        if (headers != null) {
            headers.forEach((key, value) -> post.header().add(key, value));
        }

        HttpReportModel reportModel = new HttpReportModel();
        reportModel.setAddress(HostUtils.getHostIp());
        reportModel.setHost(HostUtils.getHostName());
        reportModel.setTimestamp(System.currentTimeMillis());
        reportModel.setEngineName(model.getIdentifier());

        FlatEngine flatEngine = FlatManager.getInstance().getFlatEngine(model.getSource());
        reportModel.setMd5(flatEngine.getMd5());

        if (flatEngine.getReportContent()) {
            reportModel.setContent(flatEngine.getContent());
        }

        List<Object> allComponents = model.getAllComponents();
        JSONArray array = new JSONArray();
        for (Object com : allComponents) {
            Measurable measurable = (Measurable) com;

            JSONObject item = new JSONObject();
            item.put("metrics", measurable.getMetrics());

            if (com instanceof Component) {
                item.put("name", ((Component<?, ?>) com).getName());
                item.put("type", ((Component<?, ?>) com).getType());
            } else if (com instanceof Pipeline) {
                item.put("name", ((Pipeline<?, ?>) com).describe());
                item.put("type", "pipeline");
            } else {
                item.put("name", ((FlowEngine<?, ?>) com).getName());
                item.put("type", "engine");
            }

            array.add(item);
        }
        reportModel.setJson(array);


        String json = JSON.toJSONString(reportModel, SC, SerializerFeature.WriteEnumUsingToString);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("report engine data, engine = {}, data = {}", model.getIdentifier(), json);
        }

        post.header().add(HeaderNameEnum.CONTENT_TYPE.getName(), "application/json;charset=UTF-8").add(HeaderNameEnum.CONTENT_LENGTH.getName(), String.valueOf(bytes.length))
                .done().body().write(bytes, 0, bytes.length).done().onSuccess(httpResponse -> LOGGER.info("send statistic success, engine: {}", model.getIdentifier()))
                .onFailure(throwable -> LOGGER.info("send statistic failed, engine: {}", model.getIdentifier(), throwable));
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
