package org.smartboot.flow.core.builder;


import org.smartboot.flow.core.Condition;
import org.smartboot.flow.core.attribute.AttributeHolder;
import org.smartboot.flow.core.attribute.AttributeValueResolver;
import org.smartboot.flow.core.attribute.Attributes;
import org.smartboot.flow.core.component.ChooseComponent;
import org.smartboot.flow.core.component.Component;
import org.smartboot.flow.core.executable.Executable;
import org.smartboot.flow.core.util.AssertUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qinluo
 * @date 2022-11-11 14:54:34
 * @since 1.0.0
 */
public class ChooseBuilder<T, S> extends AbstractComponentBuilder<T, S> {

    private Condition<T, S> condition;
    private final Map<Object, Component<T, S>> branches = new ConcurrentHashMap<>();
    private Component<T, S> defaultBranch;

    public ChooseBuilder<T, S> test(Condition<T, S> condition) {
        AssertUtil.notNull(condition, "condition must not be null!");
        this.condition = condition;
        return this;
    }

    /**
     * Create a new branch in choose.
     */
    public CaseBuilder<T, S> when(Object branch) {
        AssertUtil.notNull(condition, "You must invoke test method first!");
        AssertUtil.notNull(branch, "branch must not be null");
        AssertUtil.isFalse(branches.containsKey(branch), "duplicated branch");
        return new CaseBuilder<>(this, branch);
    }

    /**
     * Called by CaseBuilder finished.
     */
    void addBranch(Object when, Component<T, S> branch) {
        branches.put(when, branch);
    }

    public ChooseBuilder<T, S> defaultBranch(Component<T, S> branch) {
        AssertUtil.notNull(condition, "You must invoke test method first!");
        this.defaultBranch = branch;
        return this;
    }

    public ChooseBuilder<T, S> defaultBranch(Executable<T, S> branch) {
        return defaultBranch(branch != null ? wrap(branch) : null);
    }

    @Override
    public ChooseComponent<T, S> build() {
        AssertUtil.notNull(condition, "condition must not be null!");
        AssertUtil.isFalse(branches.isEmpty(), "choose builder has empty branch!");

        ChooseComponent<T, S> chooseComponent = new ChooseComponent<>();
        chooseComponent.setCondition(condition);
        chooseComponent.setBranches(branches);
        chooseComponent.setDefaultBranch(defaultBranch);
        applyValues(chooseComponent);
        return chooseComponent;
    }

    @Override
    public ChooseBuilder<T, S> apply(Attributes attributes, Object value) {
        super.apply(attributes, value);
        return this;
    }

    @Override
    public ChooseBuilder<T, S> withResolver(AttributeValueResolver resolver) {
        super.withResolver(resolver);
        return this;
    }

    @Override
    public ChooseBuilder<T, S> apply(List<AttributeHolder> holders) {
        super.apply(holders);
        return this;
    }
}
