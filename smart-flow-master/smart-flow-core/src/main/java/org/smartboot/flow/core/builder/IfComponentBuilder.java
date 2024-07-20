package org.smartboot.flow.core.builder;


import org.smartboot.flow.core.Condition;
import org.smartboot.flow.core.attribute.AttributeHolder;
import org.smartboot.flow.core.attribute.AttributeValueResolver;
import org.smartboot.flow.core.attribute.Attributes;
import org.smartboot.flow.core.component.Component;
import org.smartboot.flow.core.component.IfComponent;
import org.smartboot.flow.core.executable.Executable;
import org.smartboot.flow.core.util.AssertUtil;

import java.util.List;

/**
 * @author qinluo
 * @date 2022-11-11 11:40:20
 * @since 1.0.0
 */
public class IfComponentBuilder<T, S> extends AbstractComponentBuilder<T, S>{

    /**
     * This component's condition
     */
    private Condition<T, S> condition;
    private Component<T, S> then;
    private Component<T, S> otherwise;

    @Override
    public IfComponentBuilder<T, S> apply(Attributes attributes, Object value) {
        super.apply(attributes, value);
        return this;
    }

    @Override
    public IfComponentBuilder<T, S> withResolver(AttributeValueResolver resolver) {
        super.withResolver(resolver);
        return this;
    }

    @Override
    public IfComponentBuilder<T, S> apply(List<AttributeHolder> holders) {
        super.apply(holders);
        return this;
    }

    public IfComponentBuilder<T, S> test(Condition<T, S> condition) {
        AssertUtil.notNull(condition, "condition must not be null!");
        this.condition = condition;
        return this;
    }

    public IfComponentBuilder<T, S> then(Component<T, S> component) {
        AssertUtil.notNull(condition, "You must invoke test method first!");
        this.then = component;
        return this;
    }

    public IfComponentBuilder<T, S> then(Executable<T, S> executable) {
        Component<T, S> execute = super.wrap(executable);
        return this.then(execute);
    }

    public IfComponentBuilder<T, S> otherwise(Component<T, S> component) {
        AssertUtil.notNull(condition, "You must invoke test method first!");
        this.otherwise = component;
        return this;
    }

    public IfComponentBuilder<T, S> otherwise(Executable<T, S> executable) {
        Component<T, S> execute = executable != null ? super.wrap(executable) : null;
        return this.otherwise(execute);
    }

    @Override
    public IfComponent<T, S> build() {
        AssertUtil.notNull(condition, "condition must not be null");
        AssertUtil.notNull(then, "then branch must not be null");

        IfComponent<T, S> ifComponent = new IfComponent<>();
        ifComponent.setCondition(condition);
        ifComponent.setThenComponent(then);
        ifComponent.setElseComponent(otherwise);
        applyValues(ifComponent);
        return ifComponent;
    }
}
