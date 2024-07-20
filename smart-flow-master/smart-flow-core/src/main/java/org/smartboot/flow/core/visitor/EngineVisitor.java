package org.smartboot.flow.core.visitor;

import org.smartboot.flow.core.FlowEngine;

import java.util.concurrent.Executor;

/**
 * @author qinluo
 * @date 2022-11-13 22:20:06
 * @since 1.0.0
 */
public class EngineVisitor {

    /**
     * The delegate visitor.
     */
    protected EngineVisitor delegate;

    public EngineVisitor() {
        this(null);
    }

    public EngineVisitor(EngineVisitor delegate) {
        this.delegate = delegate;
    }

    public void visitEnd() {
        if (delegate != null) {
            delegate.visitEnd();
        }
    }

    /**
     * Visit name and executor.
     *
     * @param name     engine's name.
     * @param executor async executor, maybe null.
     *
     */
    public void visit(String name, Executor executor) {
        if (delegate != null) {
            delegate.visit(name, executor);
        }
    }

    public <T, S> void visitSource(FlowEngine<T, S> flowEngine) {
        if (delegate != null) {
            delegate.visitSource(flowEngine);
        }
    }

    public PipelineVisitor visitPipeline(String pipeline) {
        if (delegate != null) {
            return delegate.visitPipeline(pipeline);
        }

        return null;
    }
}
