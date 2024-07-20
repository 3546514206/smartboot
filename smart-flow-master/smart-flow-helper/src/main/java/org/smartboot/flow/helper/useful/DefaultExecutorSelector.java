package org.smartboot.flow.helper.useful;

import org.smartboot.flow.core.FlowEngine;

import java.util.Map;
import java.util.Objects;

/**
 * @author qinluo
 * @date 2022-12-06 21:29:32
 * @since 1.0.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class DefaultExecutorSelector extends AbstractExecutorSelector {

    @Override
    public <T, S> FlowEngine<T, S> doSelect(Map<String, FlowEngine> engineMap, AbstractEngineQuery query) {
        String key = query.getKey();

        for (FlowEngine flowEngine : engineMap.values()) {
            if (Objects.equals(key, flowEngine.getName())) {
                return flowEngine;
            }
        }

        return null;
    }
}
