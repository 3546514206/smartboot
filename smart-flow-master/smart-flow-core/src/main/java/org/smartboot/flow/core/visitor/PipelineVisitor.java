package org.smartboot.flow.core.visitor;

import org.smartboot.flow.core.Pipeline;
import org.smartboot.flow.core.common.ComponentType;

/**
 * @author qinluo
 * @date 2022-11-13 22:34:58
 * @since 1.0.0
 */
public class PipelineVisitor {

    protected PipelineVisitor delegate;

    public PipelineVisitor() {
        this(null);
    }

    public PipelineVisitor(PipelineVisitor delegate) {
        this.delegate = delegate;
    }

    /**
     * Visit pipeline source.
     */
    public <T, S> void visitSource(Pipeline<T, S> pipeline) {
        if (delegate != null) {
            delegate.visitSource(pipeline);
        }
    }

    public void visitEnd() {
        if (this.delegate != null) {
            this.delegate.visitEnd();
        }
    }

    /**
     * Visit component with type, name, describe.
     */
    public ComponentVisitor visitComponent(ComponentType type, String name, String describe) {
        if (delegate != null) {
            return delegate.visitComponent(type, name, describe);
        }

        return null;
    }
}
