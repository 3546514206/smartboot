package org.smartboot.flow.manager.trace;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.core.manager.EngineManager;
import org.smartboot.flow.manager.ManagerConstants;
import org.smartboot.flow.manager.UpdateContentTask;
import org.smartboot.http.client.HttpClient;
import org.smartboot.http.client.HttpPost;
import org.smartboot.http.common.enums.HeaderNameEnum;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author qinluo
 * @date 2023-02-07
 * @since 1.1.3
 */
public class HttpTraceReporter implements TraceReporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpTraceReporter.class);

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
    public void report(TraceData trace) {
        EngineManager defaultManager = EngineManager.defaultManager();

        HttpClient httpClient = new HttpClient(host, port);
        httpClient.configuration().connectTimeout((int) timeout);
        HttpPost post = httpClient.post(ManagerConstants.REPORT_TRACE);

        if (headers != null) {
            headers.forEach((key, value) -> post.header().add(key, value));
        }

        FlowEngine<Object, Object> source = defaultManager.getEngineModel(trace.getEngineName()).getSource();
        TraceReportRequest request = TraceRequestConverter.convert(trace, source);

        String json = JSON.toJSONString(request, SerializerFeature.WriteEnumUsingToString);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("report trace data, engine = {}, data = {}", trace.getEngineName(), JSON.toJSONString(request));
        }

        post.header().add(HeaderNameEnum.CONTENT_TYPE.getName(), "application/json;charset=UTF-8").add(HeaderNameEnum.CONTENT_LENGTH.getName(), String.valueOf(bytes.length));

        // Use body stream write.
        post.body().write(bytes, 0, bytes.length).done()
                .onSuccess(httpResponse -> LOGGER.info("send trace success"))
                .onFailure(throwable -> LOGGER.error("send trace failed", throwable));
    }

    public void init() {
        URL parsedUrl;
        try {
            parsedUrl = new URL(serverAddress);
        } catch (Exception e) {
            throw new IllegalStateException("invalid url " + serverAddress, e);
        }
        this.host = parsedUrl.getHost();
        this.port = parsedUrl.getPort();
        UpdateContentTask.startTask(host, port);
    }

    public String getServerAddress() {
        return serverAddress;
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

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
