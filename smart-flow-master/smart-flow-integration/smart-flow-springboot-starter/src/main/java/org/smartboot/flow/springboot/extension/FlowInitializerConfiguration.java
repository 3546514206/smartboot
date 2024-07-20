package org.smartboot.flow.springboot.extension;

import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.smartboot.flow.manager.reload.XmlParseReloader;
import org.smartboot.flow.manager.reload.XmlSelector;
import org.smartboot.flow.spring.extension.EngineInitializer;
import org.smartboot.flow.manager.reload.Reloader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * smart flow EngineInitializer auto config
 *
 * @author huqiang
 * @since 2022/12/15 16:32
 */
@Configuration
@ConfigurationProperties(prefix = "smart.flow.initializer")
@ConditionalOnProperty(name = "smart.flow.initializer.engines")
public class FlowInitializerConfiguration {

    /**
     * Initializer engines, multiple engine name split with ,
     */
    private String engines;
    private String xmlSelector;
    private String reloader;

    @Bean(value = "springboot-engine-initializer", initMethod = "start")
    @ConditionalOnBean(Reloader.class)
    @ConditionalOnMissingBean(EngineInitializer.class)
    public EngineInitializer getInitializer(ApplicationContext ctx) {
        boolean withSelector = false;
        if (AuxiliaryUtils.isBlank(reloader)) {
            // not configured, use default reloader.
            reloader = XmlParseReloader.class.getName();
            withSelector = true;
        }

        Reloader reloaderBean = ctx.getBean(reloader, Reloader.class);

        if (withSelector) {
            XmlSelector selector = null;

            if (AuxiliaryUtils.isNotBlank(xmlSelector)) {
                selector = ctx.getBean(xmlSelector, XmlSelector.class);
            }
            // DatabaseXmlSelector has configured
            else if (ctx.containsBean("databaseXmlSelector")) {
                selector = ctx.getBean("databaseXmlSelector", XmlSelector.class);
            }

            if (selector != null) {
                ((XmlParseReloader)reloaderBean).setXmlSelector(selector);
            }
        }


        EngineInitializer initializer = new EngineInitializer();
        initializer.setReloader(reloaderBean);
        initializer.setEngines(Arrays.asList(engines.split(",")));
        return initializer;
    }


    public void setEngines(String engines) {
        this.engines = engines;
    }

    public void setXmlSelector(String xmlSelector) {
        this.xmlSelector = xmlSelector;
    }

    public void setReloader(String reloader) {
        this.reloader = reloader;
    }
}
