package org.smartboot.flow.core.invoker;

import org.smartboot.flow.core.EngineContext;
import org.smartboot.flow.core.component.Component;

/**
 * @author qinluo
 * @date 2022-12-07 21:28:41
 * @since 1.0.0
 */
public interface InvokeListener {

    /**
     * 执行完毕后通知处理
     *
     * @param component 执行的组件
     * @param context   执行上下文
     * @param <T>       入参泛型
     * @param <S>       出参泛型
     */
    <T, S> void onCompleted(Component<T, S> component, EngineContext<T, S> context);
}
