package org.smartboot.flow.core;

/**
 * 回滚接口，当组件执行失败时进行回滚
 *
 * @author qinluo
 * @date 2022-11-12 21:58:26
 * @since 1.0.0
 */
public interface Rollback<T, S> {

    /**
     * 回滚接口，当组件执行失败时进行回滚
     *
     * @param context 执行上下文
     */
    void rollback(EngineContext<T, S> context);
}
