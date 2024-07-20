package org.smartboot.flow.manager.trace;

import org.smartboot.flow.core.EngineContext;
import org.smartboot.flow.core.ExecutionListener;
import org.smartboot.flow.core.Key;
import org.smartboot.flow.core.component.Component;
import org.smartboot.flow.manager.ManagerConfiguration;

/**
 * 由管理模块进行Listener的注册
 *
 * @author qinluo
 * @date 2023/1/30 22:42
 * @since 1.0.7
 */
@SuppressWarnings("rawtypes")
public class ManagerExecutionListener implements ExecutionListener {

    /**
     * Invoke trace in engine invoking.
     */
    private static final Key<TraceData> TRACES = Key.of("traces");
    private final TraceCollector traceCollector;
    private TraceSampleStrategy sampleStrategy;

    public ManagerExecutionListener(TraceCollector traceCollector, TraceSampleStrategy sampleStrategy) {
        this.traceCollector = traceCollector;
        this.sampleStrategy = sampleStrategy;
    }

    public void setSampleStrategy(TraceSampleStrategy sampleStrategy) {
        this.sampleStrategy = sampleStrategy;
    }

    @Override
    public <T, S> void start(EngineContext<T, S> context) {
        if (sampleStrategy != null && !sampleStrategy.sampled(context)) {
            return;
        }

        TraceIdGenerator generator = TraceIdGenerator.getTraceIdGenerator();
        if (generator == null) {
            return;
        }

        // init trace.
        TraceData trace = new TraceData();
        trace.setEngineName(context.getEngineName());
        trace.setTraceId(generator.getTraceId(context));
        trace.setTraceTime(System.currentTimeMillis());

        if (ManagerConfiguration.traceRequest) {
            trace.setRequest(context.getReq());
        }

        // 暂时init个object
        context.putExt(TRACES, trace);

    }

    @Override
    public <T, S> void beforeExecute(EngineContext<T, S> context, Object object) {
        TraceData trace = context.getExt(TRACES);
        if (trace == null) {
            return;
        }

        if (object instanceof Component) {
            Component comp = (Component) object;
            trace.add(comp, new ComponentData(comp, context));
        }
    }

    @Override
    public <T, S> void afterExecute(EngineContext<T, S> context, Object object, Throwable ex) {
        ComponentData data = findDataFromContext(context, object);
        if (data == null) {
            return;
        }

        data.setEscape(System.currentTimeMillis() - data.getStart());
        data.setEx(ex);
    }

    @Override
    public <T, S> void beforeRollback(EngineContext<T, S> context, Object object) {
        ComponentData data = findDataFromContext(context, object);
        if (data == null) {
            return;
        }

        data.setRollbackStart(System.currentTimeMillis());
    }

    private <T, S> ComponentData findDataFromContext(EngineContext<T, S> context, Object object) {
        TraceData trace = context.getExt(TRACES);
        if (trace == null) {
            return null;
        }

        if (!(object instanceof Component)) {
            return null;
        }

        Component comp = (Component) object;
        return trace.getData(comp);
    }

    @Override
    public <T, S> void afterRollback(EngineContext<T, S> context, Object object) {
        ComponentData data = findDataFromContext(context, object);
        if (data == null) {
            return;
        }

        data.setRollbackEnd(System.currentTimeMillis());
    }

    @Override
    public <T, S> void completed(EngineContext<T, S> context) {
        TraceData trace = context.getExt(TRACES);
        if (trace == null) {
            return;
        }

        if (sampleStrategy != null && !sampleStrategy.sampled2(context)) {
            return;
        }

        trace.setEx(context.getFatal());
        trace.setEndTime(System.currentTimeMillis());

        if (ManagerConfiguration.traceResult) {
            trace.setResult(context.getResult());
        }

        // 将执行采集到的数据进行上报
        traceCollector.submit(trace);
    }
}
