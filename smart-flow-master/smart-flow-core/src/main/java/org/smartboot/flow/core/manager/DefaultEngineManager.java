package org.smartboot.flow.core.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.core.attribute.AttributeHolder;
import org.smartboot.flow.core.util.AssertUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qinluo
 * @date 2022/11/18 22:48
 * @since 1.0.0
 */
public class DefaultEngineManager implements EngineManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEngineManager.class);
    private static final DefaultEngineManager INSTANCE = new DefaultEngineManager();

    private final Map<String, EngineModel> registeredEngines = new ConcurrentHashMap<>();

    /**
     * Rename getDefaultManager to getInstance.
     *
     * @return 1.0.9
     */
    public static EngineManager getInstance() {
        return INSTANCE;
    }

    private DefaultEngineManager() {

    }

    @Override
    public <T, S> void register(FlowEngine<T, S> engine) {
        AssertUtil.notNull(engine, "registered engine must not be null");
        AssertUtil.notBlank(engine.getName(), "registered engine name must not be blank");

        if (registeredEngines.get(engine.getName()) != null) {
            LOGGER.warn("engine {} already registered", engine.getName());
        }

        // Ensure engine and components is valid
        engine.validate();

        RegisterEngineVisitor visitor = new RegisterEngineVisitor();
        engine.accept(visitor);

        EngineModel model = visitor.getEngine();
        registeredEngines.put(model.getIdentifier(), model);
    }

    @Override
    public EngineModel getEngineModel(String name) {
        AssertUtil.notBlank(name, "name must not be blank!");
        return registeredEngines.get(name);
    }

    @Override
    public void changeAttributes(String identifier, List<AttributeHolder> attributeHolders) {
        AssertUtil.notBlank(identifier, "identifier must not be blank!");
        if (registeredEngines.containsKey(identifier)) {
            // change engine attributes.
            return;
        }

        for (Map.Entry<String, EngineModel> entry : registeredEngines.entrySet()) {
            if (entry.getValue().containsComponent(identifier)) {
                entry.getValue().changeModelAttributes(identifier, attributeHolders);
                break;
            }
        }
    }

    @Override
    public List<String> getRegisteredEngineNames() {
        return new ArrayList<>(registeredEngines.keySet());
    }

    @Override
    public void resetStatistic(String identifier) {
        AssertUtil.notBlank(identifier, "identifier must not be blank!");
        for (Map.Entry<String, EngineModel> entry : registeredEngines.entrySet()) {
            if (entry.getKey().equals(identifier) || entry.getValue().getPipeline().getIdentifier().equals(identifier)) {
                entry.getValue().reset();
                break;
            }

        }

        for (Map.Entry<String, EngineModel> entry : registeredEngines.entrySet()) {
            if (entry.getValue().containsComponent(identifier)) {
                entry.getValue().reset(identifier);
                break;
            }

        }
    }

    @Override
    public void detachAll() {
        this.registeredEngines.clear();
    }

    @Override
    public void detach(String name) {
        AssertUtil.notBlank(name, "name must not be blank!");
        this.registeredEngines.remove(name);
    }
}
