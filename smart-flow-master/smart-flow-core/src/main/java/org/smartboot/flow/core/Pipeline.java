package org.smartboot.flow.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.attribute.Attributes;
import org.smartboot.flow.core.component.Component;
import org.smartboot.flow.core.invoker.InvokeListener;
import org.smartboot.flow.core.invoker.WrappedInvoker;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.visitor.ComponentVisitor;
import org.smartboot.flow.core.visitor.PipelineVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qinluo
 * @date 2022-11-12 21:57:43
 * @since 1.0.0
 */
public class Pipeline<T, S> implements Rollback<T, S>, Describable, Validator, Measurable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Pipeline.class);

    private final List<Component<T, S>> components = new ArrayList<>();
    private String name;
    private volatile boolean validateCalled = false;
    private volatile boolean resetCalled;

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String describe() {
        return name;
    }

    public void execute(EngineContext<T, S> context) throws Throwable {
        // Executed sequences.
        List<Component> executed = new ArrayList<>();
        context.putExt(Key.of(this), executed);

        // Enter record track
        WrappedInvoker.delegateEnter(context, this);

        for (Component<T, S> component : components)  {
            if (context.isBroken()) {
                break;
            }

            // component is disabled.
            if (!component.isEnabled()) {
                LOGGER.warn("component {} disabled in engine {}", component.describe(), context.getEngineName());
                continue;
            }

            // Ensure async dependencies are called and finished.
            ensureAllDependsExecuted(component, context);

            // Compatible-check after ensureAllDependsExecuted called.
            if (context.isBroken()) {
                break;
            }

            context.getInvoker().invoke(context, component, new InvokeListener() {
                @Override
                public <R, Q> void onCompleted(Component<R, Q> component, EngineContext<R, Q> context) {
                    // rollback
                    if (component.isRollbackable(context) && !executed.contains(component)) {
                        executed.add(component);
                    }
                }
            });
        }

        context.ensureFinished();

        // Exit record track
        WrappedInvoker.delegateExit(context, this, context.getFatal());
    }

    private void ensureAllDependsExecuted(Component<T, S> component, EngineContext<T, S> context) {

        // @since 1.1.4 ensure all async invoke has finished.
        if (component.getAttributeValue(Attributes.DEPENDS_ALL, false)) {
            context.ensureFinished();
            return;
        }

        for (String depends : component.getDependsOn()) {
            AsyncCallResult<T, S> asyncCall = context.getAsyncCall(depends);
            if (asyncCall == null) {
                EngineContext.LOGGER.warn("could not find dependsOn call on component {} with dependency {}", component.getName(), depends);
                continue;
            }

            // Await current future finished.
            asyncCall.checkAndWait(context);
            if (context.isBroken()) {
                break;
            }
        }

    }

    @Override
    public synchronized void rollback(EngineContext<T, S> context) {
        List<Component<T, S>> executed = context.remove(Key.of(this));
        if (executed == null || executed.size() == 0) {
            return;
        }

        WrappedInvoker.delegateEnter(context, this);

        // Execute rollback desc.
        for (int i = executed.size() - 1; i >= 0; i--) {
            Component<T, S> component = executed.get(i);
            try {
                WrappedInvoker.rollback(context, component);
            } catch (Exception e) {
                LOGGER.error("{} rollback failed {}", this.name, component.describe(), e);
            }
        }

        WrappedInvoker.delegateExit(context, this, null);
    }

    public boolean isRollbackable(EngineContext<T, S> context) {
        List<Component<T, S>> executed = context.getExt(Key.of(this));
        return executed != null && executed.size() != 0;
    }

    public void setComponents(List<Component<T, S>> components) {
        this.components.addAll(components);
    }

    public void accept(PipelineVisitor pipelineVisitor) {
        pipelineVisitor.visitSource(this);

        for (Component<T, S> component : components) {
            ComponentVisitor visitor = pipelineVisitor.visitComponent(component.getType(), component.getName(), component.describe());
            if (visitor != null) {
                component.accept(visitor);
            }
        }

        pipelineVisitor.visitEnd();
    }

    @Override
    public void validate() {
        if (validateCalled) {
            return;
        }

        validateCalled = true;
        AssertUtil.notNull(components, "pipeline [" + describe() + "] components must not be null");
        AssertUtil.isTrue(components.size() != 0, "pipeline [" + describe() + "] components must not be null");
        components.forEach(Validator::validate);
    }

    @Override
    public void reset() {
        if (resetCalled) {
            return;
        }

        resetCalled = true;
        Measurable.super.reset();
        for (Component<T, S> component : components) {
            component.reset();
        }

        // Reset after all components called.
        resetCalled = false;
    }
}
