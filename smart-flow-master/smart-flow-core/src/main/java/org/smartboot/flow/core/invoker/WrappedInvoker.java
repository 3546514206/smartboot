package org.smartboot.flow.core.invoker;

import org.smartboot.flow.core.Describable;
import org.smartboot.flow.core.EngineContext;
import org.smartboot.flow.core.ExecutionListener;
import org.smartboot.flow.core.ExecutionListeners;
import org.smartboot.flow.core.Feature;
import org.smartboot.flow.core.component.Component;
import org.smartboot.flow.core.component.PipelineComponent;
import org.smartboot.flow.core.util.AuxiliaryUtils;

/**
 * @author qinluo
 * @date 2023/2/20 22:06
 * @since 1.0.0
 */
public class WrappedInvoker {

    public static <T,S> int invoke(EngineContext<T, S> context, Component<T, S> component) throws Throwable {
        delegateEnter(context, component);

        // 兼容ExecutionListener内部的主动broken调用
        if (context.isBroken()) {
            ExecutionListener brokenListener = ExecutionListeners.getBrokenListener();
            int status = context.getExecuting();
            context.setExecuting(EngineContext.EXECUTING);
            if (context.cfg().isConfigured(Feature.BrokenInListenerOnTree)) {
                context.enter(Feature.BrokenInListenerOnTree.name() + "-" + brokenListener.describe());
                context.exit(Feature.BrokenInListenerOnTree.name() + "-" + brokenListener.describe());
            }
            context.setExecuting(status);
            if (checkTraceable(component)) {
                context.exit(component, null);
            }
            ExecutionListeners.remove();
            return 0;
        }

        Throwable e = null;

        try {
            return component.invoke(context);
        } catch (Throwable ex) {
            e = ex;
            throw ex;
        } finally {
            delegateExit(context, component, e);
        }
    }

    public static <T,S> void rollback(EngineContext<T, S> context, Component<T, S> component) {
        // Enter
        delegateEnter(context, component);

        Throwable e = null;

        try {
            component.rollback(context);
        } catch (Throwable ex) {
            e = ex;
            throw ex;
        } finally {
            delegateExit(context, component, e);
        }
    }

    public static <T,S> void delegateEnter(EngineContext<T, S> context, Object component) {
        if (checkTraceable(component)) {
            // Enter
            context.enter(component);
        }
        context.beforeExecute(component);
    }

    public static <T,S> void delegateExit(EngineContext<T, S> context, Object component, Throwable ex) {
        if (checkTraceable(component)) {
            context.exit(component, ex);
        }

        context.afterExecute(component, ex);
    }

    private static boolean checkTraceable(Object component) {
        boolean described = component instanceof Describable;
        if (!described) {
            return true;
        }

        // Check not subclass of PipelineComponent
        if (component instanceof PipelineComponent) {
            return false;
        }

        Describable describable = (Describable) component;
        return describable.describe() != null && !AuxiliaryUtils.isAnonymous(describable.describe());
    }
}
