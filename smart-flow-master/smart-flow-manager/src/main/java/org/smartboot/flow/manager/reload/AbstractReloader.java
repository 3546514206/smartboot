package org.smartboot.flow.manager.reload;

import java.util.List;

/**
 * @author qinluo
 * @date 2022-12-21 13:50:34
 * @since 1.0.0
 */
public abstract class AbstractReloader implements Reloader {

    @Override
    public void reload(String engineName) {
        List<ReloadListener> registered = ReloadListenerRegistry.getRegistered();
        registered.add(new LogReloadListener());
        ReloadListener listener = new ReloadListeners(registered);

        Throwable ex = null;
        try {
            listener.onload(engineName);
            this.doReload(engineName);
        } catch (Throwable e) {
            ex = e;
        } finally {
            listener.loadCompleted(engineName, ex);
        }
    }

    /**
     * Reload engine in subclass.
     *
     * @param engineName engineName.
     */
    public abstract void doReload(String engineName);
}
