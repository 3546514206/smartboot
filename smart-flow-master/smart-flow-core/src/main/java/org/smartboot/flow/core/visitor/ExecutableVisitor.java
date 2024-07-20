package org.smartboot.flow.core.visitor;

import org.smartboot.flow.core.executable.Executable;

import java.util.Map;

/**
 * @author qinluo
 * @date 2022-11-13 22:34:58
 * @since 1.0.5
 */
public class ExecutableVisitor {

    protected ExecutableVisitor delegate;

    public ExecutableVisitor() {
        this(null);
    }

    public ExecutableVisitor(ExecutableVisitor delegate) {
        this.delegate = delegate;
    }

    /**
     * Visit source.
     */
    public <T, S> void visitSource(Executable<T, S> executable) {
        if (delegate != null) {
            delegate.visitSource(executable);
        }
    }

    /**
     * Visit binding attributes that starts with <code>execute.</code>
     *
     * @since 1.1.0
     * @param attrs binding attributes.
     */
    public void visitBindingAttrs(Map<String, String> attrs) {
        if (delegate != null) {
            delegate.visitBindingAttrs(attrs);
        }
    }
}
