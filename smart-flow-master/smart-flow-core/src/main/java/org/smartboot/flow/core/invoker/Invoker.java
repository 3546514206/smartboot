package org.smartboot.flow.core.invoker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.AsyncCallResult;
import org.smartboot.flow.core.EngineContext;
import org.smartboot.flow.core.component.Component;
import org.smartboot.flow.core.util.AssertUtil;

import java.util.concurrent.Future;

/**
 * @author qinluo
 * @date 2022-12-07 20:37:25
 * @since 1.0.0
 */
public class Invoker {

    private static final Logger LOGGER = LoggerFactory.getLogger(Invoker.class);

    public <T, S> int invoke(EngineContext<T, S> context, Component<T, S> component, InvokeListener ...listeners) {
        InvokeListener listener = new InvokeListeners(listeners);
        if (component.isAsync()) {
            AssertUtil.notNull(context.getExecutor(), "executor must not be null");

            AsyncRunner runner = new AsyncRunner(() -> execute(component, context, listener));
            Future<Integer> submitted = context.getExecutor().submit(runner);
            AsyncCallResult<T, S> result = new AsyncCallResult<>();
            result.setFuture(submitted);
            result.setName(component.getName());
            result.setTimeout(component.getTimeout());
            result.setSource(component);
            result.setListeners(listener);
            context.addAsyncInvoke(result);
            return 1;
        }

        return execute(component, context, listener);
    }

    private <T, S> int execute(Component<T, S> component,
                               EngineContext<T, S> context,
                               InvokeListener listeners) {

        Throwable fatal = null;

        try {
            WrappedInvoker.invoke(context, component);
        } catch (Throwable e) {
            fatal = e;
        } finally {
            // rollback
            listeners.onCompleted(component, context);

            if (fatal != null) {
                if (component.isDegradable()) {
                    LOGGER.warn("degrade component {}", component.describe(), fatal);
                } else {
                    LOGGER.error("execute component failed {}", component.describe(), fatal);
                    context.setFatal(fatal);
                    context.setRollback(true);
                    context.broken(true);
                }
            }
        }

        return 1;
    }
}
