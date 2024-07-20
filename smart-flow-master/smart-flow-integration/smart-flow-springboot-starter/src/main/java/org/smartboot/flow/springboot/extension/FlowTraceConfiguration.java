package org.smartboot.flow.springboot.extension;

import org.smartboot.flow.core.parser.DefaultObjectCreator;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.smartboot.flow.manager.trace.DefaultTraceCollector;
import org.smartboot.flow.manager.trace.LogTraceReporter;
import org.smartboot.flow.manager.trace.TraceReporter;
import org.smartboot.flow.manager.trace.TraceSampleStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Stream;

/**
 * @author huqiang
 * @since 2022/12/14 19:15
 */
@Configuration
@ConfigurationProperties(prefix = "smart.flow.manager.trace")
@ConditionalOnProperty(value = "smart.flow.manager.trace.enabled", havingValue = "true")
public class FlowTraceConfiguration {

    /**
     * request idle in microseconds.
     */
    private long idle = 5000L;

    /* Trace Collector and Reporter */

    /**
     * Trace radio.
     */
    private double radio;

    /**
     * sampleStrategy bean name.
     */
    private String sampleStrategy;

    /**
     * sampleStrategy class name.
     */
    private String sampleStrategyClass;


    @ConditionalOnProperty(value = "smart.flow.manager.trace.log-report", havingValue = "true")
    @Bean(value = "logTraceReporter")
    public LogTraceReporter getLogTraceReporter() {
        return new LogTraceReporter();
    }

    @ConditionalOnProperty(value = "smart.flow.manager.trace.enabled", havingValue = "true")
    @Bean(value = "defaultTraceCollector", initMethod = "start")
    public DefaultTraceCollector getTraceCollector(ApplicationContext ctx){
        DefaultTraceCollector collector = new DefaultTraceCollector();
        collector.setIdle(idle);
        collector.setRadio(radio);

        String[] traceReporterNames = ctx.getBeanNamesForType(TraceReporter.class);
        Stream.of(traceReporterNames).forEach(p -> collector.addReporter(ctx.getBean(p, TraceReporter.class)));

        if (AuxiliaryUtils.isNotBlank(sampleStrategy)) {
            collector.setSampleStrategy(ctx.getBean(sampleStrategy, TraceSampleStrategy.class));
        }
        // Create strategy with classname.
        else if (AuxiliaryUtils.asClass(sampleStrategyClass) != null) {
            TraceSampleStrategy strategy = DefaultObjectCreator.getInstance().create(sampleStrategyClass, TraceSampleStrategy.class, false);
            collector.setSampleStrategy(strategy);
        }

        return collector;
    }

    /* Manager bean definition end */

    public void setIdle(long idle) {
        this.idle = idle;
    }

    public void setRadio(double radio) {
        this.radio = radio;
    }

    public void setSampleStrategy(String sampleStrategy) {
        this.sampleStrategy = sampleStrategy;
    }

    public void setSampleStrategyClass(String sampleStrategyClass) {
        this.sampleStrategyClass = sampleStrategyClass;
    }
}
