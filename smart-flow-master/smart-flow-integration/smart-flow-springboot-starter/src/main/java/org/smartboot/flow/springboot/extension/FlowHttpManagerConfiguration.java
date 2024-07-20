package org.smartboot.flow.springboot.extension;

import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.smartboot.flow.manager.change.HttpManager;
import org.smartboot.flow.manager.reload.Reloader;
import org.smartboot.flow.manager.reload.XmlParseReloader;
import org.smartboot.flow.manager.report.HttpReporter;
import org.smartboot.flow.manager.trace.HttpTraceReporter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author huqiang
 * @since 2022/12/14 19:15
 */
@Configuration
@ConfigurationProperties(prefix = "smart.flow.manager")
@ConditionalOnProperty(name = "smart.flow.manager.server")
public class FlowHttpManagerConfiguration {

    /* Common Server Configuration start */
    /**
     * Connect timeout in microseconds.
     */
    private long timeout;

    /**
     * manager server address.
     */
    private String server;

    /**
     * request idle in microseconds.
     */
    private long idle = 5000L;

    /**
     * First delay in microseconds.
     */
    private long delayAtFirst;

    private Map<String, String> headers;

    /* Common Server Configuration end */


    /* Management Configuration */

    /**
     * Management reloader's bean name.
     */
    private String reloader;

    /* Manager bean definition start */

    /**
     * Configuration httpReporter.
     *
     * @return httpReporter definition.
     */
    @ConditionalOnProperty(value = "smart.flow.manager.statistic.report", havingValue = "true")
    @Bean(value = "smart-flow-http-reporter", initMethod = "start")
    public HttpReporter getHttpReporter() {
        HttpReporter reporter = new HttpReporter();
        reporter.setTimeout(this.timeout);
        reporter.setServerAddress(this.server);
        reporter.setIdle(this.idle);
        reporter.setHeaders(headers);
        return reporter;
    }

    /**
     * Configuration http manager。
     */
    @ConditionalOnProperty(value = "smart.flow.manager.management.enabled", havingValue = "true")
    @Bean(value = "smart-flow-http-manager", initMethod = "start")
    public HttpManager getHttpManager(ApplicationContext ctx){
        HttpManager httpManager = new HttpManager();
        httpManager.setTimeout(timeout);
        httpManager.setUrl(server);
        httpManager.setIdle(idle);
        httpManager.setDelayAtFirst(delayAtFirst);
        httpManager.setHeaders(headers);

        if (AuxiliaryUtils.isBlank(reloader)) {
            // not configured, use default reloader.
            reloader = XmlParseReloader.class.getName();
        }

        Reloader reloaderBean = ctx.getBean(reloader, Reloader.class);
        httpManager.setReloader(reloaderBean);
        return httpManager;
    }

    /**
     * Configuration httpTraceReporter.
     */
    @ConditionalOnProperty(value = "smart.flow.manager.trace.http-report", havingValue = "true")
    @Bean(value = "httpTraceReporter", initMethod = "init")
    public HttpTraceReporter getHttpTraceReporter() {
        HttpTraceReporter reporter = new HttpTraceReporter();
        reporter.setTimeout(this.timeout);
        reporter.setServerAddress(this.server);
        reporter.setHeaders(headers);
        return reporter;
    }

    /* Manager bean definition end */

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setIdle(long idle) {
        this.idle = idle;
    }

    public void setDelayAtFirst(long delayAtFirst) {
        this.delayAtFirst = delayAtFirst;
    }

    public void setReloader(String reloader) {
        this.reloader = reloader;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
