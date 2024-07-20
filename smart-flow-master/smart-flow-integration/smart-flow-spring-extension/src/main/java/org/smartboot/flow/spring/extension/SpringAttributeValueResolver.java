package org.smartboot.flow.spring.extension;

import org.smartboot.flow.core.attribute.AttributeValueResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * @author qinluo
 * @date 2023-10-27 15:35:59
 * @since 1.1.4
 */
public class SpringAttributeValueResolver extends AttributeValueResolver {

    @Autowired
    private Environment environment;

    @Override
    protected String earlyResolve(String value) {
        try {
            return environment.resolvePlaceholders(value);
        } catch (Exception e) {
            return value;
        }
    }
}
