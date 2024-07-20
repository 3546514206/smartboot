package org.smartboot.flow.spring.extension;

import org.smartboot.flow.core.parser.DefaultObjectCreator;
import org.smartboot.flow.core.parser.ObjectCreator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author qinluo
 * @date 2022-12-21 14:22:23
 * @since 1.0.0
 */
public class SpringObjectCreator implements ObjectCreator, ApplicationContextAware {

    public static final String NAME = SpringObjectCreator.class.getName();

    private ApplicationContext ctx;

    @Override
    @SuppressWarnings("unchecked")
    public <T> T create(String type, Class<T> expectType, boolean useCache) {
        Object obj;

        try {
            // type as bean name.
            obj = ctx.getBean(type);
        } catch (BeansException ignored) {
            obj = DefaultObjectCreator.getInstance().create(type, expectType, useCache);
        }

        return (T)obj;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.ctx = ctx;
    }
}
