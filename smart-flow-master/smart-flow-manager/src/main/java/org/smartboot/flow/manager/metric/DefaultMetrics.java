package org.smartboot.flow.manager.metric;

import com.alibaba.fastjson.JSONObject;
import org.smartboot.flow.core.metrics.Metrics;
import org.smartboot.flow.core.util.AssertUtil;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qinluo
 * @date 2022/11/23 21:35
 * @since 1.0.7
 */
public class DefaultMetrics extends Metrics {

    protected final Map<String, Counter> COUNTERS = new ConcurrentHashMap<>();
    protected long started = System.currentTimeMillis();

    public void addMetric(String name, long value) {
        this.addMetric(MetricKind.ACCUMULATE, name, value);
    }

    public void addMetric(MetricKind kind, String name, long value) {
        AssertUtil.notNull(name, "metric name must not be null");
        AssertUtil.notNull(kind, "metric kind must not be null");

        Counter counter = COUNTERS.computeIfAbsent(name, k -> createCounter(kind));
        counter.increment(value);
    }

    public Map<String, Object> getMetrics() {
        return Collections.unmodifiableMap(COUNTERS);
    }

    private Counter createCounter(MetricKind kind) {
        if (kind == MetricKind.MAX) {
            return new MaxCounter();
        }

        return new Counter();
    }

    @Override
    public void reset() {
        COUNTERS.forEach((k, v) -> v.reset());
        this.started = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        JSONObject metrics = new JSONObject();
        metrics.put("started", started);
        COUNTERS.forEach((k, v) -> metrics.put(k, v.getValue()));

        return metrics.toJSONString();
    }
}
