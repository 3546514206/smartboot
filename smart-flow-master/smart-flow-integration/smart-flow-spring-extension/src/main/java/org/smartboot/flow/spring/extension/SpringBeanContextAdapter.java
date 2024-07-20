package org.smartboot.flow.spring.extension;

import org.smartboot.flow.core.util.BeanContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qinluo
 * @date 2022-12-21 14:22:23
 * @since 1.1.3
 */
public class SpringBeanContextAdapter implements BeanContext, ApplicationContextAware, BeanPostProcessor, Ordered {

    public static final String NAME = SpringBeanContextAdapter.class.getName();

    private ApplicationContext ctx;

    @Override
    public <T> T getBean(String name) {
        return (T)ctx.getBean(name);
    }

    @Override
    public <T> T getBean(String name, Class<T> type) {
        return ctx.getBean(name, type);
    }

    @Override
    public <T> List<T> getBean(Class<T> type) {
        String[] beanNamesForType = ctx.getBeanNamesForType(type);
        List<T> objects = new ArrayList<>();
        for (String name : beanNamesForType) {
            objects.add(ctx.getBean(name, type));
        }

        return objects;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.ctx = ctx;
        this.init();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
