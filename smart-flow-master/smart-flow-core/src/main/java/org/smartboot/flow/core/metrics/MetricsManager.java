package org.smartboot.flow.core.metrics;

import org.smartboot.flow.core.util.AssertUtil;

/**
 * @author qinluo
 * @date 2022-11-25 21:06:10
 * @since 1.0.0
 */
public class MetricsManager {

    private static MetricsCreator metricsCreator = DefaultMetricsCreator.INSTANCE;

    public static MetricsCreator getMetricsCreator() {
        return metricsCreator;
    }

    public static void setMetricsCreator(MetricsCreator metricsCreator) {
        MetricsManager.metricsCreator = metricsCreator != null ? metricsCreator : DefaultMetricsCreator.INSTANCE;
    }

    public static Metrics allocate(Object key) {
        AssertUtil.notNull(key, "key must not be null!");
        return metricsCreator.create(key);
    }
}
