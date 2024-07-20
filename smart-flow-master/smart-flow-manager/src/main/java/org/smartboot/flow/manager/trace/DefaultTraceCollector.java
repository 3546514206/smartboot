package org.smartboot.flow.manager.trace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.ExecutionListenerRegistry;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.manager.NamedThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author qinluo
 * @date 2023-02-07
 * @since 1.0.7
 */
public class DefaultTraceCollector implements TraceCollector {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTraceCollector.class);
    private final ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("export-trace"));

    /**
     * Report idle in mills.
     */
    protected long idle = 5000L;
    private Double radio;
    private TraceSampleStrategy sampleStrategy;
    private ManagerExecutionListener executionListener;

    private final BlockingQueue<TraceData> traceQueue = new ArrayBlockingQueue<>(20000);
    private final List<TraceReporter> reporters = new ArrayList<>(8);

    public long getIdle() {
        return idle;
    }

    public void setIdle(long idle) {
        AssertUtil.isTrue(idle > 0, "idle must great than zero");
        this.idle = idle;
    }

    public void addReporter(TraceReporter reporter) {
        reporters.add(reporter);
    }

    public void removeReporter(TraceReporter reporter) {
        reporters.remove(reporter);
    }

    public void removeAll() {
        reporters.clear();
    }

    public void setReporters(List<TraceReporter> reporters) {
        this.reporters.addAll(reporters);
    }

    public void export() {
        try {
            this.doExport();
        } catch (Throwable ex){
            LOGGER.error("report trace failed", ex);
        }
    }

    public void doExport() {
        List<TraceData> traces = new ArrayList<>();
        if (traceQueue.drainTo(traces) < 0) {
            return;
        }

        for (TraceData trace : traces) {
            for (TraceReporter reporter : reporters) {
                reporter.report(trace);
            }

        }
    }

    public void start() {
        // Register listener.
        if (sampleStrategy == null && radio != null && radio > 0 && radio <= 1) {
            sampleStrategy = new TraceSampleStrategy();
            sampleStrategy.setRadio(radio);
        }

        executionListener = new ManagerExecutionListener(this, sampleStrategy);
        ExecutionListenerRegistry.register(executionListener);
        executorService.setMaximumPoolSize(1);
        executorService.scheduleAtFixedRate(this::export, idle, idle, TimeUnit.MILLISECONDS);
    }

    @Override
    public void submit(TraceData item) {
        //noinspection ResultOfMethodCallIgnored
        traceQueue.offer(item);
    }

    public Double getRadio() {
        return radio;
    }

    public void setRadio(Double radio) {
        this.radio = radio;
        if (sampleStrategy != null && radio != null && radio > 0 && radio <= 1) {
            sampleStrategy.setRadio(radio);
        }
    }

    public TraceSampleStrategy getSampleStrategy() {
        return sampleStrategy;
    }

    public void setSampleStrategy(TraceSampleStrategy sampleStrategy) {
        this.sampleStrategy = sampleStrategy;
        if (this.executionListener != null) {
            this.executionListener.setSampleStrategy(this.sampleStrategy);
        }
    }
}
