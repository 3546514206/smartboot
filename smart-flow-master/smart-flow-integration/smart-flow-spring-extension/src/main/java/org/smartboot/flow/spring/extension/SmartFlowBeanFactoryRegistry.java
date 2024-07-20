package org.smartboot.flow.spring.extension;

import org.smartboot.flow.core.parser.ParserContext;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.smartboot.flow.manager.reload.XmlParseReloader;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.stream.Stream;

/**
 * @author qinluo
 * @date 2023/3/12 12:41
 * @since 1.0.0
 */
public class SmartFlowBeanFactoryRegistry implements BeanDefinitionRegistryPostProcessor {

    private BeanDefinitionRegistry registry;
    public static final String NAME = SmartFlowBeanFactoryRegistry.class.getName();

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.registry = registry;
        SmartFlowRegistrar.registerAll(registry, NAME);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanNames = beanFactory.getBeanDefinitionNames();
        Stream.of(beanNames).forEach(p -> {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(p);
            Class<?> type = AuxiliaryUtils.asClass(beanDefinition.getBeanClassName());
            if (type != null && XmlParseReloader.class.isAssignableFrom(type)) {
                PropertyValue visitor = beanDefinition.getPropertyValues().getPropertyValue("visitor");
                if (visitor == null) {
                    beanDefinition.getPropertyValues().addPropertyValue("visitor", new RuntimeBeanReference(BeanDefinitionVisitor.NAME));
                }

                PropertyValue creator = beanDefinition.getPropertyValues().getPropertyValue("objectCreator");
                if (creator == null) {
                    beanDefinition.getPropertyValues().addPropertyValue("objectCreator", new RuntimeBeanReference(SpringObjectCreator.NAME));
                }
            }
        });

        try {
            // Touch visit all parsed elements after all bean definition loaded.
            ParserContext ctx = ProxyParser.getInstance().getContext();
            if (ctx != null) {
                // Load script inside xml.
                ctx.getScriptLoaders().forEach(p -> p.load(ctx));
                BeanDefinitionVisitor visitor = new BeanDefinitionVisitor(registry, ctx);
                ctx.getRegistered().forEach(visitor::visit);
            }
        } finally {
            ProxyParser.getInstance().reset();
        }

        if (beanFactory instanceof DefaultListableBeanFactory) {
            ((DefaultListableBeanFactory)beanFactory).setAllowBeanDefinitionOverriding(true);
        }
    }
}
