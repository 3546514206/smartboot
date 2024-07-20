package org.smartboot.flow.core;

/**
 * @author qinluo
 * @date 2022-11-25 20:34:35
 * @since 1.0.0
 */
public interface ExecutionListener extends Describable {

    ExecutionListener NOOP = new ExecutionListener() {};

    /**
     * 开始执行
     * @param ctx ctx 上下文
     */
    default <T, S> void start(EngineContext<T, S> ctx) {

    }

    /**
     * 结束执行
     * @param ctx ctx 上下文
     */
    default  <T, S> void completed(EngineContext<T, S> ctx) {

    }

    /**
     * 开始执行组件分支
     *
     * @param ctx ctx 上下文
     * @param object 组件/分支
     */
    default <T, S> void beforeExecute(EngineContext<T, S> ctx, Object object) {

    }

    /**
     * 结束执行组件分支
     *
     * @param ctx ctx 上下文
     * @param object 组件/分支
     * @param ex  执行错误信息
     */
    default <T, S> void afterExecute(EngineContext<T, S> ctx, Object object, Throwable ex) {

    }

    /**
     * 开始回滚组件分支
     *
     * @param ctx ctx 上下文
     * @param object 组件/分支
     */
    default <T, S> void beforeRollback(EngineContext<T, S> ctx, Object object) {

    }

    /**
     * 结束执行组件分支
     *
     * @param ctx ctx 上下文
     * @param object 组件/分支
     */
    default <T, S> void afterRollback(EngineContext<T, S> ctx, Object object) {

    }

    /**
     * Do something after registered.
     *
     * @since 1.0.9
     */
    default void doAfterRegister() {

    }

    /**
     * Do something after unregistered
     *
     * @since 1.0.9
     */
    default void doAfterUnregister() {

    }
}
