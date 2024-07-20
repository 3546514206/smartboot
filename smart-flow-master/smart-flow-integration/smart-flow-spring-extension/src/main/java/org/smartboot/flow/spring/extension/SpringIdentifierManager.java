package org.smartboot.flow.spring.extension;

import org.smartboot.flow.core.DefaultIdentifierManager;
import org.smartboot.flow.core.IdentifierManager;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * @author qinluo
 * @date 2022-11-17 12:50:02
 * @since 1.0.0
 */
public class SpringIdentifierManager implements IdentifierManager {

    private final BeanDefinitionRegistry registry;

    public SpringIdentifierManager(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    private final IdentifierManager identifierManager = new DefaultIdentifierManager();

    @Override
    public String allocate(String prefix) {
        String identifier = identifierManager.allocate(prefix);
        while (registry.containsBeanDefinition(identifier)) {
            identifier = identifierManager.allocate(prefix);
        }

        return identifier;
    }
}
