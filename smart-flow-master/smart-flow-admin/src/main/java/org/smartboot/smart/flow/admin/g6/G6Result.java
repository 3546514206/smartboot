package org.smartboot.smart.flow.admin.g6;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qinluo
 * @date 2023/2/10 20:23
 * @since 1.0.0
 */
public class G6Result {

    private Long id;
    private String name;
    private String process;
    private String request;
    private String result;
    private String traceTime;
    private String endTime;
    private String host;
    private String address;
    private String traceId;
    private String message;
    private String md5;
    private final List<Node> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();
    private List<Combo> combos = new ArrayList<>();

    public List<Node> getNodes() {
        return nodes;
    }

    public void addNode(Node n) {
        this.nodes.add(n);
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public List<Combo> getCombos() {
        return combos;
    }

    public void setCombos(List<Combo> combos) {
        this.combos = combos;
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

    public String getTraceTime() {
        return traceTime;
    }

    public void setTraceTime(String traceTime) {
        this.traceTime = traceTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
