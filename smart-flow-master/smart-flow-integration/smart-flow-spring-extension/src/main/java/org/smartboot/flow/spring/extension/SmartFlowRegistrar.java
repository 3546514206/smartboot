package org.smartboot.flow.spring.extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.manager.reload.XmlParseReloader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qinluo
 * @date 2023/1/31 22:15
 * @since 1.0.0
 */
public class SmartFlowRegistrar implements ImportBeanDefinitionRegistrar {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartFlowRegistrar.class);

    private static final Map<String, Class<?>> registeredClasses = new HashMap<>();

    static {
        registeredClasses.put(NotifierProcessor.NAME, NotifierProcessor.class);
        registeredClasses.put(SmartFlowBeanFactoryRegistry.NAME, SmartFlowBeanFactoryRegistry.class);
        registeredClasses.put(BeanDefinitionVisitor.NAME, BeanDefinitionVisitor.class);
        registeredClasses.put(SpringObjectCreator.NAME, SpringObjectCreator.class);
        registeredClasses.put(SpringBeanContextAdapter.NAME, SpringBeanContextAdapter.class);
        registeredClasses.put(XmlParseReloader.class.getName(), XmlParseReloader.class);
        registeredClasses.put(SpringAttributeValueResolver.class.getName(), SpringAttributeValueResolver.class);
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registerAll(registry);
    }

    public static void registerAll(BeanDefinitionRegistry registry, String ...excludes) {
        List<String> excludeBeans = excludes != null ? Arrays.asList(excludes) : Collections.emptyList();

        registeredClasses.forEach((k, v) -> {
            if (registry.containsBeanDefinition(k) || excludeBeans.contains(k)) {
                return;
            }
            RootBeanDefinition definition = new RootBeanDefinition();
            definition.setBeanClass(v);
            registry.registerBeanDefinition(k, definition);
            LOGGER.info("register to spring container [{}]", k);
        });
    }
}
