package org.smartboot.flow.manager.trace;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.manager.FlatEngine;
import org.smartboot.flow.manager.FlatManager;
import org.smartboot.flow.manager.HostUtils;
import org.smartboot.flow.manager.ManagerConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * @author qinluo
 * @date 2023-08-02 09:05:51
 * @since 1.1.3
 */
public class TraceRequestConverter {

    public static TraceReportRequest convert(TraceData trace, FlowEngine<?, ?> engine) {
        TraceReportRequest request = new TraceReportRequest();
        request.setAddress(HostUtils.getHostIp());
        request.setHost(HostUtils.getHostName());
        request.setTimestamp(System.currentTimeMillis());
        FlatEngine flatEngine = FlatManager.getInstance().getFlatEngine(engine);
        request.setMd5(flatEngine.getMd5());
        request.setTraceId(trace.getTraceId());
        request.setEngineName(trace.getEngineName());
        request.setSuccess(trace.getEx() == null);
        request.setTraceTime(trace.getTraceTime());
        request.setEndTime(trace.getEndTime());
        if (!request.getSuccess()) {
            request.setEx(serialExToString(trace.getEx()));
        }
        request.setRequest(toJSON(trace.getRequest()));
        request.setResult(toJSON(trace.getResult()));

        if (flatEngine.getReportContent()) {
            request.setContent(flatEngine.getContent());
        }

        JSONArray ja = new JSONArray();

        trace.getComponents().forEach((k, v) -> {
            JSONObject item = new JSONObject();
            item.put("name", k.getName());
            item.put("type", k.getType());
            item.put("start", v.getStart());
            item.put("escape", v.getEscape());
            item.put("ex", serialExToString(v.getEx()));
            item.put("rollbackStart", v.getRollbackStart());
            item.put("rollbackEnd", v.getRollbackEnd());

            ja.add(item);
        });

        request.setJson(ja);
        return request;
    }

    private static String serialExToString(Throwable ex) {
        if (ex == null) {
            return null;
        }

        int maxDepth = ManagerConfiguration.reportMaxStackDepth;
        StackTraceElement[] stackTrace = ex.getStackTrace();
        if (stackTrace.length > maxDepth) {
            StackTraceElement[] newTrace = new StackTraceElement[maxDepth];
            System.arraycopy(stackTrace, 0, newTrace, 0, newTrace.length);
            ex.setStackTrace(newTrace);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(bos);
        ex.printStackTrace(writer);
        writer.flush();
        return bos.toString();
    }

    private static String toJSON(Object obj) {
        return obj == null ? null : JSON.toJSONString(obj);
    }

}
