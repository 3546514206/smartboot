package org.smartboot.flow.core.metrics;

/**
 * @author qinluo
 * @date 2022-11-25 21:51:52
 * @since 1.0.0
 */
public class DefaultMetricsCreator extends AbstractManagedMetricsCreator {

    public static final MetricsCreator INSTANCE = new DefaultMetricsCreator();

    @Override
    public Metrics doCreate(Object key) {
        return new Metrics();
    }
}
