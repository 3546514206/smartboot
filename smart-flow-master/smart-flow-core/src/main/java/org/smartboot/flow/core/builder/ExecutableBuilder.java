package org.smartboot.flow.core.builder;


import org.smartboot.flow.core.attribute.AttributeHolder;
import org.smartboot.flow.core.attribute.AttributeValueResolver;
import org.smartboot.flow.core.attribute.Attributes;
import org.smartboot.flow.core.component.Component;
import org.smartboot.flow.core.executable.Executable;
import org.smartboot.flow.core.executable.ExecutableAdapter;
import org.smartboot.flow.core.util.AssertUtil;

import java.util.List;

/**
 * @author qinluo
 * @date 2022-11-11 21:49:01
 * @since 1.0.0
 */
public class ExecutableBuilder<T, S> extends AbstractComponentBuilder<T, S> {

    private Executable<T, S> executable;

    public ExecutableBuilder<T, S> executable(Executable<T, S> executable) {
        AssertUtil.notNull(executable, "executable must not be null");
        this.executable = executable;
        return this;
    }

    @Override
    public Component<T, S> build() {
        AssertUtil.notNull(executable, "executable must not be null");
        ExecutableAdapter<T, S> adapter = new ExecutableAdapter<>(executable);
        super.applyValues(adapter);
        return adapter;
    }

    @Override
    public ExecutableBuilder<T, S> apply(Attributes attributes, Object value) {
        super.apply(attributes, value);
        return this;
    }

    @Override
    public ExecutableBuilder<T, S> withResolver(AttributeValueResolver resolver) {
        super.withResolver(resolver);
        return this;
    }

    @Override
    public ExecutableBuilder<T, S> apply(List<AttributeHolder> holders) {
        super.apply(holders);
        return this;
    }
}
