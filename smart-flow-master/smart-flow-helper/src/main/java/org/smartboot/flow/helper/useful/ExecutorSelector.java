package org.smartboot.flow.helper.useful;


import org.smartboot.flow.core.FlowEngine;

import java.util.Map;

/**
 * @author qinluo
 * @version 1.0.0
 * @since 2019-06-04 15:55
 */
@SuppressWarnings("rawtypes")
public interface ExecutorSelector {

    /**
     * 从候选执行引擎中选择一个返回
     * @param engineMap   执行引擎列表Map
     * @param query       查询对象
     * @return            选中的执行引擎
     */
    <T, S> FlowEngine<T, S> select(Map<String, FlowEngine> engineMap, AbstractEngineQuery query);
}
