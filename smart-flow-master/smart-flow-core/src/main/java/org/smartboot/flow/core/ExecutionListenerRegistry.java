package org.smartboot.flow.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.util.AssertUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author qinluo
 * @date 2022-11-25 21:15:22
 * @since 1.0.0
 */
public class ExecutionListenerRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionListenerRegistry.class);
    private static final List<ExecutionListener> REGISTERED = new ArrayList<>();

    /*
     * Load from SPI location and install.
     */
    static {
        try {
            ServiceLoader<ExecutionListener> loader = ServiceLoader.load(ExecutionListener.class);
            for (ExecutionListener next : loader) {
                register(next);
            }
        } catch (Exception e) {
            LOGGER.error("load ExecutionListener from META-INF failed", e);
        }
    }

    public synchronized static void register(ExecutionListener listener) {
        AssertUtil.notNull(listener, "listener must not be null");
        if (!REGISTERED.contains(listener)) {
            REGISTERED.add(listener);
            listener.doAfterRegister();
        }
    }

    public synchronized static void unregister(ExecutionListener listener) {
        AssertUtil.notNull(listener, "listener must not be null");
        if (REGISTERED.remove(listener)) {
            listener.doAfterUnregister();
        }
    }

    public static List<ExecutionListener> getRegistered() {
        return new ArrayList<>(REGISTERED);
    }
}
