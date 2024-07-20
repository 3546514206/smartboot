package org.smartboot.flow.manager.reload;

/**
 * @author qinluo
 * @date 2022-12-21 13:45:27
 * @since 1.0.0
 */
public interface Reloader {

    /**
     * Reload engine
     *
     * @param engineName engineName.
     */
    void reload(String engineName);
}
