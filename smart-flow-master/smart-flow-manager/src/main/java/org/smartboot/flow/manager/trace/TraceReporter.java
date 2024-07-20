package org.smartboot.flow.manager.trace;

/**
 * @author qinluo
 * @date 2023-08-02 09:13:20
 * @since 1.1.3
 */
public interface TraceReporter {

    /**
     * Report trace record.
     *
     * @param trace trace.
     */
    void report(TraceData trace);
}
