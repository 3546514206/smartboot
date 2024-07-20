package org.smartboot.flow.spring.extension;

import org.smartboot.flow.core.NamedCondition;
import org.springframework.beans.factory.BeanNameAware;

/**
 * @author qinluo
 * @date 2022-11-11 21:57:29
 * @since 1.0.0
 */
public abstract class NameAwareCondition<T, S> extends NamedCondition<T, S> implements BeanNameAware {

    @Override
    public void setBeanName(String name) {
        super.setName(name);
    }
}
