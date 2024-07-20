package org.smartboot.flow.manager.trace;

/**
 * @author qinluo
 * @date 2023-02-07
 * @since 1.1.3
 */
public interface TraceCollector {

    /**
     * Submit trace data to TraceCollector.
     *
     * @param traceData traceData.
     */
    void submit(TraceData traceData);
}
