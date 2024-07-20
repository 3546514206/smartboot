package org.smartboot.flow.core.util;

import org.smartboot.flow.core.exception.FlowException;

import java.util.Objects;

/**
 * @author qinluo
 * @date 2022/11/13
 * @since 1.0.0
 */
public final class AssertUtil {

    private AssertUtil() {
        shouldNotReachHere();
    }


    public static void shouldNotReachHere() {
        throw new FlowException("should not reach here");
    }

    public static void notNull(Object value, String message) {
        if (value == null) {
            throw new FlowException(message);
        }
    }

    public static void assertNull(Object value, String message) {
        if (value != null) {
            throw new FlowException(message);
        }
    }

    public static void notBlank(String value, String message) {
        if (value == null || value.trim().length() == 0) {
            throw new FlowException(message);
        }
    }

    public static void isTrue(boolean value, String message) {
        if (!value) {
            throw new FlowException(message);
        }
    }

    public static void isFalse(boolean value, String message) {
        if (value) {
            throw new FlowException(message);
        }
    }

    public static void assertEquals(Object expect, Object actual, String message) {
        if (!Objects.equals(expect, actual)) {
            throw new FlowException(message);
        }
    }
}
