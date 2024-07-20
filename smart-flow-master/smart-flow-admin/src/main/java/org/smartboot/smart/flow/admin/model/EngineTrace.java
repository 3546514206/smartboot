package org.smartboot.smart.flow.admin.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;

/**
 * @author qinluo
 * @date 2023-01-30
 * @since 1.0.7
 */
public class EngineTrace implements Serializable {
    private static final long serialVersionUID = 5630916400064848439L;

    private Long id;
    private String engineName;
    private String content;
    private String md5;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date reportTime;
    private String address;
    private String host;
    private String traceId;
    private Integer status;
    private String message;
    private String request;
    private String result;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date traceTime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    private long escaped;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date created;
    private Date updated;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Date getReportTime() {
        return reportTime;
    }

    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
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

    public Date getTraceTime() {
        return traceTime;
    }

    public void setTraceTime(Date traceTime) {
        this.traceTime = traceTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public long getEscaped() {
        return escaped;
    }

    public void setEscaped(long escaped) {
        this.escaped = escaped;
    }
}
