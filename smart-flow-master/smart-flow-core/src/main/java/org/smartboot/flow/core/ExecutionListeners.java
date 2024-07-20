package org.smartboot.flow.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author qinluo
 * @date 2022-11-25 21:54:14
 * @since 1.0.0
 */
public class ExecutionListeners implements ExecutionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionListeners.class);

    /**
     * 记录中断流程的Listener
     *
     * @since 1.1.4
     */
    private static final ThreadLocal<ExecutionListener> BROKEN = new ThreadLocal<>();

    private final List<ExecutionListener> listeners;

    public ExecutionListeners(List<ExecutionListener> listeners) {
        this.listeners = listeners;
    }

    public void addLast(ExecutionListener listener) {
        listeners.add(listener);
    }

    public void addFirst(ExecutionListener listener) {
        listeners.add(0, listener);
    }

    public static ExecutionListener getBrokenListener() {
        return BROKEN.get();
    }

    public static void remove() {
        // Remove lastest broken listener.
        BROKEN.remove();
    }

    @Override
    public <T, S> void start(EngineContext<T, S> context) {
        for (ExecutionListener listener : listeners) {
            try {
                listener.start(context);
            } catch (Throwable e) {
                LOGGER.warn("execute listener {} failed", listener.getClass().getName(), e);
            }
        }
    }

    @Override
    public <T, S> void completed(EngineContext<T, S> context) {
        for (ExecutionListener listener : listeners) {
            try {
                listener.completed(context);
            } catch (Throwable e) {
                LOGGER.warn("execute listener {} failed", listener.getClass().getName(), e);
            }
        }
    }

    @Override
    public <T, S> void beforeExecute(EngineContext<T, S> context, Object object) {
        for (ExecutionListener listener : listeners) {
            try {
                listener.beforeExecute(context, object);
                // Already broken.
                if (context.isBroken()) {
                    BROKEN.set(listener);
                    return;
                }

            } catch (Throwable e) {
                LOGGER.warn("execute listener {} failed", listener.getClass().getName(), e);
            }
        }
    }

    @Override
    public <T, S> void afterExecute(EngineContext<T, S> context, Object object, Throwable ex) {
        for (ExecutionListener listener : listeners) {
            try {
                listener.afterExecute(context, object, ex);
            } catch (Throwable e) {
                LOGGER.warn("execute listener {} failed", listener.getClass().getName(), e);
            }
        }
    }

    @Override
    public <T, S> void beforeRollback(EngineContext<T, S> context, Object object) {
        for (ExecutionListener listener : listeners) {
            try {
                listener.beforeRollback(context, object);
            } catch (Throwable e) {
                LOGGER.warn("execute listener {} failed", listener.getClass().getName(), e);
            }
        }
    }

    @Override
    public <T, S> void afterRollback(EngineContext<T, S> context, Object object) {
        for (ExecutionListener listener : listeners) {
            try {
                listener.afterRollback(context, object);
            } catch (Throwable e) {
                LOGGER.warn("execute listener {} failed", listener.getClass().getName(), e);
            }
        }
    }
}
