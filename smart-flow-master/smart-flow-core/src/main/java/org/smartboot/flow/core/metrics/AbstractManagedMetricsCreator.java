package org.smartboot.flow.core.metrics;

import org.smartboot.flow.core.util.AssertUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qinluo
 * @date 2022-11-25 21:51:52
 * @since 1.0.9
 */
public abstract class AbstractManagedMetricsCreator implements MetricsCreator{

    protected final Map<Object, Metrics> created = new ConcurrentHashMap<>();

    protected Metrics getCreated(Object key) {
        return created.get(key);
    }

    protected void cacheCreated(Object key, Metrics metrics) {
        this.created.put(key, metrics);
    }

    @Override
    public Metrics create(Object key) {
        AssertUtil.notNull(key, "key must not be null!");
        Metrics metrics = this.getCreated(key);
        if (metrics == null) {
            metrics = this.doCreate(key);
            AssertUtil.notNull(metrics, "doCreate metrics failed " + this.getClass().getName());
            this.cacheCreated(key, metrics);
        }

        return metrics;
    }

    /**
     * Create metrics object.
     *
     * @param key key
     * @return    metrics
     */
    public abstract Metrics doCreate(Object key);
}
