package org.smartboot.flow.core;

/**
 * @author qinluo
 * @date 2022-11-28 20:10:46
 * @since 1.0.0
 */
public interface DegradeCallback<T, S> extends Describable {

    /**
     * 降级回调通知
     *
     * @param context 执行上下文
     * @param e       当前组件出现的异常
     */
    default void doWithDegrade(EngineContext<T, S> context, Throwable e) {

    }
}
