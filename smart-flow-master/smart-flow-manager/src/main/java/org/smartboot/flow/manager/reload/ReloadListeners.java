package org.smartboot.flow.manager.reload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author qinluo
 * @date 2022-12-21 13:52:37
 * @since 1.0.0
 */
public class ReloadListeners implements ReloadListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReloadListeners.class);

    private final List<ReloadListener> listeners;

    public ReloadListeners(List<ReloadListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public void onload(String engineName) {
        for (ReloadListener listener : listeners) {

            try {
                listener.onload(engineName);
            } catch (Exception e) {
                LOGGER.warn("execute reload listener failed", e);
            }
        }
    }

    @Override
    public void loadCompleted(String engineName, Throwable e) {
        for (ReloadListener listener : listeners) {
            try {
                listener.loadCompleted(engineName, e);
            } catch (Exception ex) {
                LOGGER.warn("execute reload listener failed", ex);
            }
        }
    }
}
