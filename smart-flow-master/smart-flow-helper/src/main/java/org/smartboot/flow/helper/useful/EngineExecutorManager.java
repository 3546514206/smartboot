package org.smartboot.flow.helper.useful;



import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.core.Validator;
import org.smartboot.flow.core.exception.FlowException;
import org.smartboot.flow.core.util.AssertUtil;

import java.util.Map;

/**
 * 引擎管理器抽象类
 *
 * @author qinluo
 * @version 1.0.0
 * @since 2019-05-20 14:18
 */
@SuppressWarnings({"rawtypes"})
public class EngineExecutorManager implements Validator {

    /**
     * Managed engines.
     */
    private Map<String, FlowEngine> flowEngines;

    /**
     * Default selector by name.
     */
    private ExecutorSelector selector = new DefaultExecutorSelector();

    public ExecutorSelector getSelector() {
        return selector;
    }

    public void setSelector(ExecutorSelector selector) {
        if (selector == null) {
            throw new IllegalStateException("selector must not be null!");
        }
        this.selector = selector;
    }

    public void setSelectorName(String selectorName) {
        if (selector instanceof AbstractExecutorSelector) {
            ((AbstractExecutorSelector) selector).setSelectorName(selectorName);
        }
    }

    public Map<String, FlowEngine> getFlowEngines() {
        return flowEngines;
    }

    public void setFlowEngines(Map<String, FlowEngine> flowEngines) {
        this.flowEngines = flowEngines;
    }

    public <K, S> FlowEngine<K, S> getEngine(AbstractEngineQuery query) {
        AssertUtil.notNull(query, "engine query must not be null!");

        this.validate();

        FlowEngine<K, S> engineExecutor = selector.select(flowEngines, query);
        if (engineExecutor == null) {
            throw new FlowException("select engine executor error. selector return null!");
        }

        return engineExecutor;
    }

    public <K, S> FlowEngine<K, S> getEngine(String engineName) {
        AssertUtil.notBlank(engineName, "engineName must not be blank");
        return getEngine(new DefaultEngineQuery(engineName));
    }

    @Override
    public void validate() {
        if (flowEngines == null || flowEngines.size() == 0) {
            throw new FlowException("there is no available engine, please check!");
        }

        AssertUtil.notNull(selector, "selector must not be null!");
    }
}
