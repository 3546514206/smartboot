package org.smartboot.flow.core.exception;

/**
 * @author qinluo
 * @date 2022-11-11 21:10:03
 * @since 1.0.0
 */
public class FlowException extends RuntimeException {

    private static final long serialVersionUID = -7613637640003340281L;

    public FlowException(String message) {
        super(message);
    }

    public FlowException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlowException(Throwable cause) {
        super(cause);
    }
}
