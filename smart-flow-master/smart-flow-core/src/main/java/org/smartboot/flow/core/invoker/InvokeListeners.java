package org.smartboot.flow.core.invoker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.EngineContext;
import org.smartboot.flow.core.component.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author qinluo
 * @date 2022-12-07 21:37:59
 * @since 1.0.0
 */
public class InvokeListeners implements InvokeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvokeListeners.class);

    private final List<InvokeListener> invokeListeners;

    public InvokeListeners(InvokeListener ...invokeListeners) {
        if (invokeListeners == null || invokeListeners.length == 0) {
            this.invokeListeners = new ArrayList<>(0);
        } else {
            this.invokeListeners = Stream.of(invokeListeners).filter(Objects::nonNull).collect(Collectors.toList());
        }
    }

    @Override
    public <T, S> void onCompleted(Component<T, S> component, EngineContext<T, S> context) {
        for (InvokeListener listener : invokeListeners) {

            try {
                listener.onCompleted(component, context);
            } catch (Exception e) {
                LOGGER.warn("invoke listener failed, listener = {}", listener.getClass().getName(), e);
            }
        }
    }
}
