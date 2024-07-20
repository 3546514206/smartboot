package org.smartboot.flow.core;

import org.smartboot.flow.core.component.Component;
import org.smartboot.flow.core.invoker.InvokeListener;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author qinluo
 * @date 2022-11-13 21:53:53
 * @since 1.0.0
 */
public class AsyncCallResult<T, S> {

    private String name;
    private long timeout;
    private Future<Integer> future;
    private Component<T, S> source;
    private InvokeListener listeners;
    private volatile boolean called;

    public void setListeners(InvokeListener listeners) {
        this.listeners = listeners;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public Future<Integer> getFuture() {
        return future;
    }

    public void setFuture(Future<Integer> future) {
        this.future = future;
    }

    public Component<?, ?> getSource() {
        return source;
    }

    public void setSource(Component<T, S> source) {
        this.source = source;
    }

    public synchronized void checkAndWait(EngineContext<T, S> context) {
        if (called) {
            return;
        }

        called = true;

        try {
            this.future.get(this.timeout, TimeUnit.MILLISECONDS);
        } catch (Throwable e) {
            if (source.isDegradable()) {
                EngineContext.LOGGER.warn("degrade component {}", source.getName(), e);
            } else {
                EngineContext.LOGGER.error("wait component async-execute timeout {}ms {}", timeout, source.describe(), e);
                context.setFatal(e);
                context.setRollback(true);
                context.broken(true);
            }
        } finally {
            listeners.onCompleted(source, context);
        }

    }
}
