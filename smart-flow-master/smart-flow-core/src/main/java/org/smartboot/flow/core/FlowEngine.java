package org.smartboot.flow.core;


import org.smartboot.flow.core.exception.DefaultExceptionHandler;
import org.smartboot.flow.core.exception.ExceptionHandler;
import org.smartboot.flow.core.invoker.Invoker;
import org.smartboot.flow.core.manager.EngineManager;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.visitor.EngineVisitor;
import org.smartboot.flow.core.visitor.PipelineVisitor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * @author qinluo
 * @date 2022-11-12 21:58:12
 * @since 1.0.0
 */
public class FlowEngine<T, S> implements Describable, Validator, Measurable {

    private Pipeline<T, S> pipeline;
    private ExceptionHandler exceptionHandler = new DefaultExceptionHandler();
    private String name;
    private volatile boolean validateCalled = false;

    /**
     * Start timestamp
     *
     * @since 1.0.5
     */
    private final long startedAt = System.currentTimeMillis();

    /** Used in async invoking */
    private ExecutorService executor;

    public EngineContext<T, S> execute(T t) {
        return execute(t, null);
    }

    public EngineContext<T, S> execute(T t, S s) {
        EngineContext<T, S> context = new EngineContext<>();
        context.setReq(t);
        context.setResult(s);
        return execute(context);
    }

    public EngineContext<T, S> execute(EngineContext<T, S> context) {
        AssertUtil.notNull(context, "context must not be null!");
        // Validate engine before called.
        validate();

        initContext(context);

        // fire start
        start(context);

        context.enter(this);

        boolean rollback = false;
        try {
            pipeline.execute(context);
        } catch (Throwable e) {
            context.setFatal(e);
            rollback = true;
        }

        if (rollback || context.getRollback()) {
            context.setExecuting(EngineContext.ROLLBACK);
            pipeline.rollback(context);
        }

        // For end flow-engine.
        context.setExecuting(EngineContext.EXECUTING);
        context.exit(this);

        // complete execute.
        complete(context);

        if (context.getFatal() != null && exceptionHandler != null) {
            context.getHandler().handle(context, context.getFatal());
        }

        return context;
    }

    private void start(EngineContext<T, S> context) {
        EngineContextHelper.set(context);
        context.executedAt = System.currentTimeMillis();
        context.listener.start(context);
    }

    private void complete(EngineContext<T, S> context) {
        context.completedAt = System.currentTimeMillis();
        context.listener.completed(context);
        EngineContextHelper.remove();
    }

    protected void initContext(EngineContext<T, S> context) {
        context.clear();
        context.setHandler(exceptionHandler);
        context.executor = executor;
        context.setEngineName(this.name);
        context.setExecuting(EngineContext.EXECUTING);
        context.setInvoker(new Invoker());
        context.cfg = SmartFlowConfiguration.newCfg();

        // Execution Listener.
        ExecutionListeners listeners = new ExecutionListeners(ExecutionListenerRegistry.getRegistered());
        if (context.cfg.isConfigured(Feature.RecordTrace, Feature.PrintTrace)) {
            listeners.addLast(PrintExecutionTraceTreeListener.getInstance());
        }

        context.setListener(listeners);
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public Pipeline<T, S> getPipeline() {
        return pipeline;
    }

    public void setPipeline(Pipeline<T, S> pipeline) {
        this.pipeline = pipeline;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    /**
     * Return flow-engine created timestamp.
     *
     * @return timestamp
     * @since 1.0.5
     */
    public long getStartedAt() {
        return startedAt;
    }

    @Override
    public String describe() {
        return "flow-engine##" + name;
    }

    public void accept(EngineVisitor engineVisitor) {
        AssertUtil.notNull(engineVisitor, "visitor must not be null!");

        engineVisitor.visit(this.name, this.executor);
        engineVisitor.visitSource(this);

        PipelineVisitor pipelineVisitor = engineVisitor.visitPipeline(pipeline.describe());
        if (pipelineVisitor != null) {
            pipeline.accept(pipelineVisitor);
        }

        // Visit completed.
        engineVisitor.visitEnd();
    }

    @Override
    public void validate() {
        if (validateCalled) {
            return;
        }

        AssertUtil.notBlank(name, "engine's name must not be null");
        AssertUtil.notNull(pipeline, "engine[ " + name + " ]pipeline must not be null");
        pipeline.validate();
        validateCalled = true;
        EngineManager defaultManager = EngineManager.defaultManager();
        defaultManager.detach(name);
        defaultManager.register(this);
    }

    @Override
    public void reset() {
        Measurable.super.reset();
        pipeline.reset();
    }
}
