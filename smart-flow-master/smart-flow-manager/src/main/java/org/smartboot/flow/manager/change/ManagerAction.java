package org.smartboot.flow.manager.change;

import java.util.Objects;

/**
 * @author qinluo
 * @date 2022/11/22 22:02
 * @since 1.0.0
 */
public enum ManagerAction {
    /**
     * Change component attributes.
     */
    CHANGE_ATTRIBUTES,

    /**
     * Reset statistic metrics.
     */
    RESET_METRICS,

    /**
     * Reload engine.
     */
    RELOAD,

    ;

    public static ManagerAction get(String name) {
        if (Objects.equals(name, CHANGE_ATTRIBUTES.name())) {
            return CHANGE_ATTRIBUTES;
        } else if (Objects.equals(name, RESET_METRICS.name())) {
            return RESET_METRICS;
        } else if (Objects.equals(name, RELOAD.name())) {
            return RELOAD;
        }

        return null;
    }
}
