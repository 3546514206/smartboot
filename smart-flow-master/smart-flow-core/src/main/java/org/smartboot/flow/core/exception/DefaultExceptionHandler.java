package org.smartboot.flow.core.exception;


import org.smartboot.flow.core.EngineContext;

/**
 * @author qinluo
 * @date 2022-11-11 21:11:59
 * @since 1.0.0
 */
public class DefaultExceptionHandler implements ExceptionHandler {

    @Override
    public <T, S> void handle(EngineContext<T, S> context, Throwable e) {
        throw new FlowException(e);
    }
}
