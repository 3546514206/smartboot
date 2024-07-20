package org.smartboot.flow.core.builder;


import org.smartboot.flow.core.attribute.AttributeHolder;
import org.smartboot.flow.core.attribute.AttributeValueResolver;
import org.smartboot.flow.core.attribute.Attributes;
import org.smartboot.flow.core.component.Component;
import org.smartboot.flow.core.executable.Executable;
import org.smartboot.flow.core.executable.ExecutableAdapter;
import org.smartboot.flow.core.util.AssertUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qinluo
 * @date 2022-11-13 17:49:01
 * @since 1.0.0
 */
public abstract class AbstractComponentBuilder<T, S> {

    private AttributeValueResolver valueResolver = AttributeValueResolver.getInstance();

    /**
     * Component attribute attributes.
     */
    private final Map<Attributes, Object> settings = new HashMap<>(8);

    public AbstractComponentBuilder<T, S> withResolver(AttributeValueResolver resolver) {
        AssertUtil.notNull(resolver, "Resolver must not be null");
        this.valueResolver = resolver;
        return this;
    }

    public AbstractComponentBuilder<T, S> apply(Attributes attributes, Object value) {
        AssertUtil.notNull(attributes, "Unknown attribute");
        AssertUtil.notNull(value, "null");

        Object resolved = this.valueResolver.resolve(attributes, value);

        AssertUtil.isTrue(attributes.accept(resolved), "Un-matched type");
        this.settings.put(attributes, resolved);
        return this;
    }

    public AbstractComponentBuilder<T, S> apply(List<AttributeHolder> holders) {
        if (holders != null && holders.size() > 0) {
            holders.forEach(p -> apply(p.getAttribute(), p.getValue()));
        }

        return this;
    }

    /**
     * Build component.
     *
     * @return component.
     */
    public abstract Component<T, S> build();

    /**
     * Apply settings.
     */
    protected void applyValues(Component<T, S> component) {
        settings.forEach((k, v) -> k.apply(component, v));
        component.setValueResolver(valueResolver);
    }

    /**
     * Created as pure component.
     */
    public Component<T, S> wrap(Executable<T, S> executable) {
        AssertUtil.notNull(executable, "executable must not be null");
        return new ExecutableAdapter<>(executable);
    }

}
