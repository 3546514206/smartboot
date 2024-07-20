package org.smartboot.flow.spring.extension;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qinluo
 * @date 2022-12-06 20:47:23
 * @since 1.0.0
 */
public class BeanDefinitionRegister {

    private final Map<String, BeanDefinition> registered = new HashMap<>(32);

    /**
     * Spring registry
     */
    private final BeanDefinitionRegistry registry;

    public BeanDefinitionRegister(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public BeanDefinition getBeanDefinition(String identifier) {
        BeanDefinition beanDefinition = registered.get(identifier);
        return beanDefinition != null ? beanDefinition : registry.getBeanDefinition(identifier);
    }

    public void registerBeanDefinition(String identifier, BeanDefinition def) {
        registerBeanDefinition(identifier, def, false);
    }

    public void registerBeanDefinition(String identifier, BeanDefinition def, boolean registerToSpring) {
        if (registerToSpring) {
            registry.registerBeanDefinition(identifier, def);
        } else {
            registered.put(identifier, def);
        }
    }

    public boolean isSpringBean(String identifier) {
        return registered.get(identifier) == null;
    }
}
