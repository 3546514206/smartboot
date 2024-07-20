package org.smartboot.flow.spring.extension;

import org.smartboot.flow.core.executable.AbstractExecutable;
import org.springframework.beans.factory.BeanNameAware;

/**
 * @author qinluo
 * @date 2022-11-11 21:57:29
 * @since 1.0.0
 */
public abstract class NamedAbstractExecutable<T, S> extends AbstractExecutable<T, S> implements BeanNameAware {

    private String beanName;

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public String describe() {
        return this.beanName;
    }
}
