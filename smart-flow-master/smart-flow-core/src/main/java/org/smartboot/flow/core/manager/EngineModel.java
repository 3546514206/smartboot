package org.smartboot.flow.core.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.core.attribute.AttributeHolder;
import org.smartboot.flow.core.common.Uniqueness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qinluo
 * @date 2022/11/18 22:50
 * @since 1.0.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class EngineModel extends Uniqueness {

    private static final Logger LOGGER = LoggerFactory.getLogger(EngineModel.class);

    private PipelineModel pipeline;
    private transient final Map<String, ComponentModel> components = new ConcurrentHashMap<>();
    /**
     * All flat components in engine, include pipeline and engine.
     */
    private transient final List<Object> flatComponents = new ArrayList<>();
    private transient FlowEngine source;

    public EngineModel(String name) {
        // Engine's name must be global unique.
        this.identifier = name;
    }

    public <T, S> FlowEngine<T, S> getSource() {
        return (FlowEngine<T, S>)source;
    }

    void setSource(FlowEngine source) {
        this.source = source;
    }

    public PipelineModel getPipeline() {
        return pipeline;
    }

    void setPipeline(PipelineModel pipeline) {
        this.pipeline = pipeline;
    }

    public Map<String, ComponentModel> getComponents() {
        return new HashMap<>(components);
    }

    public boolean containsComponent(String identifier) {
        return components.containsKey(identifier);
    }

    public void changeModelAttributes(String identifier, List<AttributeHolder> holders) {
        ComponentModel model = components.get(identifier);
        if (model == null) {
            LOGGER.warn("change component attributes failed, identifier = {}", identifier);
            return;
        }

        model.changeAttributes(holders);
    }

    void collect() {
        this.components.putAll(this.pipeline.collect());
    }

    public void reset(String identifier) {
        ComponentModel model = components.get(identifier);
        if (model == null) {
            LOGGER.warn("change component attributes failed, identifier = {}", identifier);
            return;
        }

        model.reset();
    }

    public void reset() {
        this.source.reset();
    }

    void init(Set<Object> visited) {
        this.flatComponents.addAll(visited);
    }

    public List<Object> getAllComponents() {
        return new ArrayList<>(flatComponents);
    }
}
