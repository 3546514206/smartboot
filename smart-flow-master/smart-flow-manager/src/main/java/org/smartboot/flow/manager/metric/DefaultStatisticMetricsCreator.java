package org.smartboot.flow.manager.metric;

import org.smartboot.flow.core.metrics.AbstractManagedMetricsCreator;
import org.smartboot.flow.core.metrics.Metrics;
import org.smartboot.flow.core.metrics.MetricsCreator;

/**
 * @author qinluo
 * @date 2022/11/23 21:41
 * @since 1.0.9
 */
public class DefaultStatisticMetricsCreator extends AbstractManagedMetricsCreator {

    static final MetricsCreator INSTANCE = new DefaultStatisticMetricsCreator();

    private DefaultStatisticMetricsCreator() {

    }

    @Override
    public Metrics doCreate(Object key) {
        return new DefaultMetrics();
    }
}
