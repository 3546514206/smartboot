package org.smartboot.flow.manager.trace;

import org.smartboot.flow.core.component.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qinluo
 * @date 2023/2/8 1:00
 * @since 1.0.7
 */
@SuppressWarnings("rawtypes")
public class TraceData {

    private Object request;
    private Object result;
    private String engineName;
    private String traceId;
    private Throwable ex;
    private long traceTime;
    private long endTime;
    private final Map<Component, ComponentData> components = new ConcurrentHashMap<>();

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public Throwable getEx() {
        return ex;
    }

    public void setEx(Throwable ex) {
        this.ex = ex;
    }

    public Map<Component, ComponentData> getComponents() {
        return components;
    }

    public void add(Component comp, ComponentData data) {
        components.put(comp, data);
    }

    public ComponentData getData(Component comp) {
        return components.get(comp);
    }

    public Object getRequest() {
        return request;
    }

    public void setRequest(Object request) {
        this.request = request;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
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
