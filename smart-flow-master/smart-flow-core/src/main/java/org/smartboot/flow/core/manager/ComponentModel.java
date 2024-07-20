package org.smartboot.flow.core.manager;

import org.smartboot.flow.core.attribute.AttributeValueResolver;
import org.smartboot.flow.core.common.ComponentType;
import org.smartboot.flow.core.common.Uniqueness;
import org.smartboot.flow.core.attribute.AttributeHolder;
import org.smartboot.flow.core.component.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qinluo
 * @date 2022/11/18 21:08
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public class ComponentModel extends Uniqueness {

    private Component component;
    private final String name;
    private final Map<String, ComponentModel> components = new ConcurrentHashMap<>();
    private final ComponentType type;
    PipelineModel pipeline;
    private List<AttributeHolder> holders = new ArrayList<>();

    ComponentModel(ComponentType type, String name) {
        this.name = name;
        this.type = type;
        this.identifier = name;
    }

    public void setHolders(List<AttributeHolder> holders) {
        this.holders = holders;
    }

    Map<String, ComponentModel> collect() {
        this.component.setName(name);
        if (type == ComponentType.SUBPROCESS) {
            return pipeline.collect();
        } else if (type != ComponentType.BASIC){
            Map<String, ComponentModel> all = new HashMap<>(32);
            components.forEach((k, v) -> {
                Map<String, ComponentModel> collected = v.collect();
                if (collected != null) {
                    all.putAll(collected);
                }
                all.put(v.getIdentifier(), v);

            });

            return all;
        } else {
            // Basic component.
            return null;
        }
    }

    void addComponent(ComponentModel model) {
        this.components.put(model.getIdentifier(), model);
    }

    @SuppressWarnings("unchecked")
    public void changeAttributes(List<AttributeHolder> holders) {
        AttributeValueResolver valueResolver = AttributeValueResolver.getInstance();
        List<AttributeHolder> backup = new ArrayList<>(this.holders);

        try {
            holders.forEach(p -> {
                Object resolvedValue = valueResolver.resolve(p.getAttribute(), p.getValue());
                p.getAttribute().apply(component, resolvedValue);
                // Remove and add new.
                backup.removeIf(o -> p.getAttribute() == o.getAttribute());
                backup.add(p);
            });

        } catch (Exception ignored) {

        } finally {
            this.holders.clear();
            this.holders.addAll(backup);
        }

    }

    public <T, S> void setComponent(Component<T, S> component) {
        this.component = component;
    }

    public void reset() {
        this.component.reset();
    }
}
