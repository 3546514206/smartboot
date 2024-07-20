package org.smartboot.flow.core.metrics;

/**
 * @author qinluo
 * @date 2022-11-25 21:50:10
 * @since 1.0.0
 */
public interface MetricsCreator {

    /**
     * 创建一个metrics实例
     *
     * @param key related-object.
     *
     * @return metrics
     */
    Metrics create(Object key);
}
