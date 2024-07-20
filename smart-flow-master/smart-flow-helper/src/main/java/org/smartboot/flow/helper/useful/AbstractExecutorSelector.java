package org.smartboot.flow.helper.useful;


import org.smartboot.flow.core.FlowEngine;

import java.util.Map;

/**
 * @author qinluo
 * @version 1.0.0
 * @date 2020-04-07 15:13
 */
@SuppressWarnings({"rawtypes"})
public abstract class AbstractExecutorSelector implements ExecutorSelector {

    private String selectorName;

    public String getSelectorName() {
        return selectorName;
    }

    public void setSelectorName(String selectorName) {
        this.selectorName = selectorName;
    }

    @Override
    public <T, S> FlowEngine<T, S> select(Map<String, FlowEngine> engineMap, AbstractEngineQuery query) {
        if (engineMap == null || engineMap.size() == 0) {
            throw new IllegalStateException(selectorName + " there are no available executors, because map is empty!");
        }

        return doSelect(engineMap, query);
    }

    /**
     * Select a engine executor.
     *
     * @param engineMap   candidate engine executor
     * @param query       engine query
     * @return            engine executor
     */
    public abstract <T, S> FlowEngine<T, S> doSelect(Map<String, FlowEngine> engineMap, AbstractEngineQuery query);
}
