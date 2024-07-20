package org.smartboot.flow.manager.trace;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.core.manager.EngineManager;

/**
 * @author qinluo
 * @date 2023-02-07
 * @since 1.1.3
 */
public class LogTraceReporter implements TraceReporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogTraceReporter.class);

    @Override
    public void report(TraceData trace) {
        EngineManager defaultManager = EngineManager.defaultManager();

        FlowEngine<Object, Object> source = defaultManager.getEngineModel(trace.getEngineName()).getSource();
        TraceReportRequest request = TraceRequestConverter.convert(trace, source);

        LOGGER.info("{}", JSON.toJSONString(request));
    }
}
