package org.smartboot.flow.core.visitor;

import org.smartboot.flow.core.ExtensionAttribute;
import org.smartboot.flow.core.attribute.AttributeHolder;
import org.smartboot.flow.core.common.ComponentType;
import org.smartboot.flow.core.component.Component;

import java.util.List;

/**
 * @author qinluo
 * @date 2022-11-13 22:38:17
 * @since 1.0.0
 */
public class ComponentVisitor {

    protected ComponentVisitor delegate;

    public ComponentVisitor() {
        this(null);
    }

    public ComponentVisitor(ComponentVisitor visitor) {
        this.delegate = visitor;
    }

    public void visitAttributes(List<AttributeHolder> attributes) {
        if (delegate != null) {
            delegate.visitAttributes(attributes);
        }
    }

    public void visitExtensionAttributes(List<ExtensionAttribute> extensionAttributes) {
        if (delegate != null) {
            delegate.visitExtensionAttributes(extensionAttributes);
        }
    }

    public PipelineVisitor visitPipeline(String pipeline) {
        if (delegate != null) {
            return delegate.visitPipeline(pipeline);
        }

        return null;
    }

    public <T, S> void visitSource(Component<T, S> component) {
        if (delegate != null) {
            delegate.visitSource(component);
        }
    }

    public ConditionVisitor visitCondition(String condition) {
        if (delegate != null) {
            return delegate.visitCondition(condition);
        }
        return null;
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

    public ComponentVisitor visitBranch(Object branch, ComponentType type, String name, String describe) {
        if (delegate != null) {
            return delegate.visitBranch(branch, type, name, describe);
        }

        return null;
    }

    public ExecutableVisitor visitExecutable(String executable) {
        if (delegate != null) {
            return delegate.visitExecutable(executable);
        }

        return null;
    }

    public void visitEnd() {
        if (this.delegate != null) {
            this.delegate.visitEnd();
        }
    }
}
