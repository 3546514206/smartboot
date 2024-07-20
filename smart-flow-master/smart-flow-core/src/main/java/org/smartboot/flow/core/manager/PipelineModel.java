package org.smartboot.flow.core.manager;

import org.smartboot.flow.core.common.Uniqueness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qinluo
 * @date 2022/11/18 22:51
 * @since 1.0.0
 */
public class PipelineModel extends Uniqueness {

    private final List<ComponentModel> components = new ArrayList<>();
    private final String name;

    PipelineModel(String name, String identifier) {
        this.name = name;
        this.identifier = identifier;
    }

    public List<ComponentModel> getComponents() {
        return new ArrayList<>(components);
    }

    void addComponent(ComponentModel component) {
        this.components.add(component);
    }

    public String getName() {
        return name;
    }

    Map<String, ComponentModel> collect() {
        HashMap<String, ComponentModel> collected = new HashMap<>(components.size());

        for (ComponentModel model : components) {
            Map<String, ComponentModel> subcollected = model.collect();
            if (subcollected != null) {
                collected.putAll(subcollected);
            }
            collected.put(model.getIdentifier(), model);
        }

        return collected;
    }

}
