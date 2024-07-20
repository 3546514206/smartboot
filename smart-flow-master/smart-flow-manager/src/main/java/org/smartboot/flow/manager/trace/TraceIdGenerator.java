package org.smartboot.flow.manager.trace;

import org.smartboot.flow.core.EngineContext;

import java.util.UUID;

/**
 * @author qinluo
 * @date 2023/1/30 22:37
 * @since 1.0.0
 */
public class TraceIdGenerator {

    private static TraceIdGenerator traceIdGenerator = new TraceIdGenerator();

    public <T, S> String getTraceId(EngineContext<T, S> ctx) {
        // 默认UUID实现
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static TraceIdGenerator getTraceIdGenerator() {
        return traceIdGenerator;
    }

    public static void setTraceIdGenerator(TraceIdGenerator generator) {
        traceIdGenerator = generator;
    }
}
