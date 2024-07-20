package org.smartboot.flow.manager.reload;

import org.smartboot.flow.core.util.AssertUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qinluo
 * @date 2022-11-25 21:15:22
 * @since 1.0.0
 */
public class ReloadListenerRegistry {

    private static final List<ReloadListener> REGISTERED = new ArrayList<>();

    public synchronized static void register(ReloadListener listener) {
        AssertUtil.notNull(listener, "listener must not be null");

        if (!REGISTERED.contains(listener)) {
            REGISTERED.add(listener);
        }
    }

    public static List<ReloadListener> getRegistered() {
        return new ArrayList<>(REGISTERED);
    }
}
