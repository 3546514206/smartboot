package org.smartboot.flow.spring.extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * @author qinluo
 * @date 2023/1/31 22:15
 * @since 1.0.0
 */
public class NotifierProcessor implements BeanPostProcessor, BeanFactoryAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotifierProcessor.class);
    public static final String NAME = SmartFlowBeanFactoryRegistry.class.getName();

    private ReflectionNotifier notifier;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        extraNotifyFields(bean, beanName);

        return bean;
    }

    private void extraNotifyFields(Object bean, String beanName) {
        try {
            ReflectionUtils.doWithFields(bean.getClass(), field -> {
                field.setAccessible(true);
                ReloadNotify conf = field.getAnnotation(ReloadNotify.class);
                if (FlowEngine.class.isAssignableFrom(field.getType())) {
                    String engineName = AuxiliaryUtils.or(conf.value()[0], field.getName());
                    notifier.addNotifyField(field, bean, Collections.singletonList(engineName));
                } else if (Map.class.isAssignableFrom(field.getType())) {
                    notifier.addNotifyField(field, bean, Arrays.asList(conf.value()));
                }

            }, field -> field.isAnnotationPresent(ReloadNotify.class));
        } catch (Exception e) {
            LOGGER.error("process bean failed, bean = {}", beanName, e);
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        notifier = new ReflectionNotifier(beanFactory);
        notifier.register();
    }
}
