package org.smartboot.flow.manager.trace;

import java.io.Serializable;

/**
 * @author qinluo
 * @date 2022-11-25 21:38:04
 * @since 1.0.7
 */
public class TraceReportRequest implements Serializable {
    private static final long serialVersionUID = -457007982997285755L;

    private long timestamp;
    private String address;
    private String host;
    private String engineName;
    private String md5;
    private String content;
    private String traceId;
    private Serializable json;
    private boolean success;
    private String ex;
    private String request;
    private String result;
    private long traceTime;
    private long endTime;

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getEx() {
        return ex;
    }

    public void setEx(String ex) {
        this.ex = ex;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Serializable getJson() {
        return json;
    }

    public void setJson(Serializable json) {
        this.json = json;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public long getTraceTime() {
        return traceTime;
    }

    public void setTraceTime(long traceTime) {
        this.traceTime = traceTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
