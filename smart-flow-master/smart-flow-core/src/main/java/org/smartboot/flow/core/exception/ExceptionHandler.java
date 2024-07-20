package org.smartboot.flow.core.exception;

import org.smartboot.flow.core.EngineContext;

/**
 * @author qinluo
 * @date 2022-11-11 21:10:38
 * @since 1.0.0
 */
public interface ExceptionHandler {

    /**
     * 处理异常
     *
     * @param context 执行上下文
     * @param e       抛出的异常
     * @param <T>     入参泛型
     * @param <S>     出参泛型
     */
    <T, S> void handle(EngineContext<T, S> context, Throwable e);
}
