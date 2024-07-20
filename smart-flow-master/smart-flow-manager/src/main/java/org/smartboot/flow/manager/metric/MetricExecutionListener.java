package org.smartboot.flow.manager.metric;

import org.smartboot.flow.core.EngineContext;
import org.smartboot.flow.core.ExecutionListener;
import org.smartboot.flow.core.Key;
import org.smartboot.flow.core.Measurable;
import org.smartboot.flow.core.metrics.Metrics;
import org.smartboot.flow.core.metrics.MetricsManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qinluo
 * @date 2022-11-25 21:23:28
 * @since 1.0.0
 */
public class MetricExecutionListener implements ExecutionListener {

    private static final ExecutionListener INSTANCE = new MetricExecutionListener();

    private MetricExecutionListener() {
        // Singleton instance.
    }

    public static ExecutionListener getInstance() {
        return INSTANCE;
    }

    @Override
    public void doAfterRegister() {
        MetricsManager.setMetricsCreator(DefaultStatisticMetricsCreator.INSTANCE);
    }

    @Override
    public void doAfterUnregister() {
        MetricsManager.setMetricsCreator(null);
    }

    @Override
    public <T, S> void beforeExecute(EngineContext<T, S> context, Object object) {
        if (!(object instanceof Measurable)) {
            return;
        }

        Measurable measurable = (Measurable) object;
        Metrics metrics = measurable.getMetrics();
        if (!DefaultMetrics.class.isAssignableFrom(metrics.getClass())) {
            return;
        }

        Map<Object, Long> escaped = context.getExt(Key.of(this));
        if (escaped == null) {
            escaped = new ConcurrentHashMap<>(32);
            context.putExt(Key.of(this), escaped);
        }

        ((DefaultMetrics)metrics).addMetric(NamedMetrics.EXECUTE, 1);
        escaped.put(object, System.currentTimeMillis());
    }

    @Override
    public <T, S> void afterExecute(EngineContext<T, S> context, Object object, Throwable ex) {
        if (!(object instanceof Measurable)) {
            return;
        }

        Measurable measurable = (Measurable) object;
        Metrics m = measurable.getMetrics();
        if (!DefaultMetrics.class.isAssignableFrom(m.getClass())) {
            return;
        }

        Map<Object, Long> escaped = context.getExt(Key.of(this));
        if (escaped == null) {
            return;
        }

        Long start = escaped.remove(object);
        if (start == null) {
            return;
        }

        DefaultMetrics metrics = (DefaultMetrics) m;
        if (ex != null) {
            metrics.addMetric(NamedMetrics.FAIL, 1);
        }
        long now = System.currentTimeMillis();
        metrics.addMetric(NamedMetrics.TOTAL_ESCAPE, (now - start));
        metrics.addMetric(MetricKind.MAX, NamedMetrics.MAX_ESCAPE, (now - start));
    }

    @Override
    public <T, S> void beforeRollback(EngineContext<T, S> context, Object object) {
        if (!(object instanceof Measurable)) {
            return;
        }

        Measurable measurable = (Measurable) object;
        Metrics m = measurable.getMetrics();
        if (!DefaultMetrics.class.isAssignableFrom(m.getClass())) {
            return;
        }

        Map<Object, Long> escaped = context.getExt(Key.of(this));
        if (escaped == null) {
            return;
        }

        DefaultMetrics metrics = (DefaultMetrics) m;
        metrics.addMetric(NamedMetrics.ROLLBACK, 1);
        escaped.put(object, System.currentTimeMillis());
    }

    @Override
    public <T, S> void afterRollback(EngineContext<T, S> context, Object object) {
        if (!(object instanceof Measurable)) {
            return;
        }

        Measurable measurable = (Measurable) object;
        Metrics m = measurable.getMetrics();
        if (!DefaultMetrics.class.isAssignableFrom(m.getClass())) {
            return;
        }

        Map<Object, Long> escaped = context.getExt(Key.of(this));
        if (escaped == null) {
            return;
        }

        Long start = escaped.remove(object);
        if (start == null) {
            return;
        }

        DefaultMetrics metrics = (DefaultMetrics) m;
        long now = System.currentTimeMillis();
        metrics.addMetric(NamedMetrics.ROLLBACK_TOTAL_ESCAPE, (now - start));
        metrics.addMetric(MetricKind.MAX, NamedMetrics.ROLLBACK_MAX_ESCAPE, (now - start));
    }
}
