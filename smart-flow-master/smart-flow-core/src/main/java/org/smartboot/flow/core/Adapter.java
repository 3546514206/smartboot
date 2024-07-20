package org.smartboot.flow.core;


import org.smartboot.flow.core.common.Pair;

/**
 * 适配器组件
 * <p>
 * 应用场景：将两个参数类型不同的业务组件适配成统一的类型
 *
 * </p>
 *
 * @author huqiang
 * @since 2022/12/7 14:35
 */
public interface Adapter<T, S, P, Q> extends Describable {


    /**
     * 适配前处理
     *
     * @param context 流程上下文
     * @return 返回需要适配的组件的入参和出参
     */
    Pair<P, Q> before(EngineContext<T, S> context);

    /**
     * 适配后置处理
     *
     * @param origin     原流程上下文
     * @param newContext 适配的组件返回的上下文
     */
    void after(EngineContext<T, S> origin, EngineContext<P, Q> newContext);

}
