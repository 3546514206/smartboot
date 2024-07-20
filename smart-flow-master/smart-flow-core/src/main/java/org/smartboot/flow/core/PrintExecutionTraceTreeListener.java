package org.smartboot.flow.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qinluo
 * @date 2023-04-12 21:36:13
 * @since 1.0.9
 */
class PrintExecutionTraceTreeListener implements ExecutionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrintExecutionTraceTreeListener.class);

    private static final ExecutionListener INSTANCE = new PrintExecutionTraceTreeListener();

    public static ExecutionListener getInstance() {
        return INSTANCE;
    }

    @Override
    public <T, S> void completed(EngineContext<T, S> ctx) {
        if (ctx.cfg.isConfigured(Feature.RecordTrace, Feature.PrintTrace)) {
            LOGGER.info("invoke trace tree: \n{}", ctx.getTrace());
        }
    }
}
