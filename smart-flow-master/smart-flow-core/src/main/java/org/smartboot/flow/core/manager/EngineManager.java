package org.smartboot.flow.core.manager;

import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.core.attribute.AttributeHolder;

import java.util.Collections;
import java.util.List;

/**
 * @author qinluo
 * @date 2022/11/18 21:07
 * @since 1.0.0
 */
public interface EngineManager {

    /**
     * Register an engine instance to manager.
     *
     * @param engine engine instance
     * @param <T>    request raw type
     * @param <S>    result raw type
     */
    <T, S> void register(FlowEngine<T, S> engine);

    /**
     * Get an engine model with engine's name.
     *
     * @param name name
     * @return     engine model
     */
    EngineModel getEngineModel(String name);

    /**
     * Dynamic change component attributes. {@link org.smartboot.flow.core.manager.EngineModel#getComponents()}
     *
     * @param identifier        component identifier
     * @param attributeHolders  be changed attributes.
     */
    void changeAttributes(String identifier, List<AttributeHolder> attributeHolders);

    /**
     * Returns all registered engine's name.
     *
     * @return engine's names.
     */
    List<String> getRegisteredEngineNames();

    /**
     * Dynamic change component attributes.
     *
     * @param identifier   component identifier
     * @param holder       be changed attribute.
     */
    default void changeAttributes(String identifier, AttributeHolder holder) {
        this.changeAttributes(identifier, Collections.singletonList(holder));
    }

    /**
     * Reset statistic data.
     *
     * @param identifier component identifier
     */
    void resetStatistic(String identifier);

    /**
     * Make all engine detach from manager.
     */
    void detachAll();

    /**
     * Make specific engine detach from manager
     *
     * @param name engine name.
     */
    void detach(String name);

    /**
     * Return default engine manager instance.
     *
     * @since 1.0.9
     * @return default engine manager instance
     */
    static EngineManager defaultManager() {
        return DefaultEngineManager.getInstance();
    }

    /* Static delegate methods */

    /**
     * @since 1.1.3
     *
     * @param name engine name.
     * @param <T>  request generic type
     * @param <S>  response generic type
     * @return     engine
     */
    static <T, S> FlowEngine<T, S> getEngine(String name) {
        EngineModel model = DefaultEngineManager.getInstance().getEngineModel(name);
        if (model != null) {
            return model.getSource();
        }
        return null;
    }
}
