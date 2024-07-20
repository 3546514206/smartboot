package org.smartboot.smart.flow.admin.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author qinluo
 * @date 2023/2/9 23:08
 * @since 1.0.7
 */
public class ReportQuery implements Serializable {


    private static final long serialVersionUID = -5035190533172786720L;

    private String engineName;
    private String host;
    private String traceId;
    private Integer pageNo = 1;
    private Integer pageSize = 10;
    private Integer start;
    private List<String> hosts;
    private String md5;
    private Date startTime;

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
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

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
}
