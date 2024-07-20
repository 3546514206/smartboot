package org.smartboot.flow.manager.reload;

/**
 * @author qinluo
 * @date 2022-12-06 20:57:37
 * @since 1.0.0
 */
public interface ReloadListener {

    /**
     * 引擎reload开始通知
     *
     * @param engineName 引擎名称
     */
    default void onload(String engineName) {

    }

    /**
     * 引擎reload完成通知
     *
     * @param engineName 引擎名称
     * @param e          异常信息，若成功，则为null
     */
    void loadCompleted(String engineName, Throwable e);

    /**
     * Register this to registry.
     */
    default void register() {
        ReloadListenerRegistry.register(this);
    }
}
