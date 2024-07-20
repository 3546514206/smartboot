package org.smartboot.flow.core;

/**
 * @author qinluo
 * @date 2022-11-11 21:57:29
 * @since 1.0.0
 */
public abstract class NamedCondition<T, S> extends Condition<T, S> {

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String describe() {
        return this.name;
    }
}
