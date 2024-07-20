package org.smartboot.flow.core.executable;


import org.smartboot.flow.core.EngineContext;
import org.smartboot.flow.core.visitor.ExecutableVisitor;

import java.util.HashMap;
import java.util.Map;

/**
 * Executable decorate class.
 *
 * @author qinluo
 * @date 2022-11-12 21:29:01
 * @since 1.1.0
 */
public final class DecorateExecutable<T, S> implements Executable<T, S> {

    private Executable<T, S> delegate;
    private Map<String, String> bindingAttrs = new HashMap<>(8);

    public Executable<T, S> getDelegate() {
        return delegate;
    }

    public void setDelegate(Executable<T, S> delegate) {
        this.delegate = delegate;
    }

    public Map<String, String> getBindingAttrs() {
        return bindingAttrs;
    }

    public void setBindingAttrs(Map<String, String> bindingAttrs) {
        this.bindingAttrs = bindingAttrs;
    }

    @Override
    public String describe() {
        return delegate.describe();
    }

    @Override
    public void rollback(EngineContext<T, S> context) {
        this.delegate.rollback(context);
    }

    @Override
    public void execute(EngineContext<T, S> context) {
        this.delegate.execute(context);
    }

    @Override
    public void visit(ExecutableVisitor visitor) {
        if (this.bindingAttrs != null) {
            visitor.visitBindingAttrs(bindingAttrs);
        }

        this.delegate.visit(visitor);
    }
}
