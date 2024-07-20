package org.smartboot.flow.core.builder;

import org.smartboot.flow.core.Adapter;
import org.smartboot.flow.core.attribute.AttributeHolder;
import org.smartboot.flow.core.attribute.AttributeValueResolver;
import org.smartboot.flow.core.attribute.Attributes;
import org.smartboot.flow.core.component.AdapterComponent;
import org.smartboot.flow.core.component.Component;
import org.smartboot.flow.core.util.AssertUtil;

import java.util.List;

/**
 * @author huqiang
 * @since 2022/12/7 19:36
 */
public class AdapterBuilder<T, S, P, Q> extends AbstractComponentBuilder<T, S> {

    /**
     * 参数与结果适配器
     */
    private Adapter<T, S, P, Q> adapter;

    /**
     * 适配组件，可以是个子流程
     */
    private Component<P, Q> component;

    public AdapterBuilder<T, S, P, Q> adapter(Adapter<T, S, P, Q> adapter) {
        AssertUtil.notNull(adapter, "adapter must not be null!");
        this.adapter = adapter;
        return this;
    }

    public AdapterBuilder<T, S, P, Q> component(Component<P, Q> component) {
        AssertUtil.notNull(component, "component must not be null!");
        this.component = component;
        return this;
    }

    @Override
    public AdapterComponent<T, S, P, Q> build() {
        AdapterComponent<T, S, P, Q> adapterComponent = new AdapterComponent<>();
        adapterComponent.setAdapter(adapter);
        adapterComponent.setComponent(component);
        applyValues(adapterComponent);
        return adapterComponent;
    }

    /**
     * Resolve return type.
     */
    @Override
    public AdapterBuilder<T, S, P, Q> apply(Attributes attributes, Object value) {
        super.apply(attributes, value);
        return this;
    }

    @Override
    public AdapterBuilder<T, S, P, Q> withResolver(AttributeValueResolver resolver) {
        super.withResolver(resolver);
        return this;
    }

    @Override
    public AdapterBuilder<T, S, P, Q> apply(List<AttributeHolder> holders) {
        super.apply(holders);
        return this;
    }
}
