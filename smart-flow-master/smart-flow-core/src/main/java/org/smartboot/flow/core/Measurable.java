package org.smartboot.flow.core;

import org.smartboot.flow.core.metrics.Metrics;
import org.smartboot.flow.core.metrics.MetricsManager;

/**
 * @author qinluo
 * @date 2022-11-25 20:31:12
 * @since 1.0.0
 */
public interface Measurable {

    /**
     * 获取可度量的指标数据
     *
     * @return 指标数据
     */
    default Metrics getMetrics() {
        return MetricsManager.allocate(this);
    }

    /**
     * Reset metrics
     */
    default void reset() {
        Metrics metrics = getMetrics();
        if (metrics != null) {
            metrics.reset();
        }
    }
}
