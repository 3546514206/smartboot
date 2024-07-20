package org.smartboot.flow.core;

/**
 * @author qinluo
 * @date 2022-11-11 21:14:29
 * @since 1.0.0
 */
public interface Describable {

    /**
     * Return self's description.
     *
     * @return description.
     */
    default String describe() {
        return this.getClass().getName();
    }
}
