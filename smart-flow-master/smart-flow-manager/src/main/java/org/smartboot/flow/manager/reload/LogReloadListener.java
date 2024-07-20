package org.smartboot.flow.manager.reload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qinluo
 * @date 2022-12-21 17:48:55
 * @since 1.0.0
 */
public class LogReloadListener implements ReloadListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogReloadListener.class);

    @Override
    public void onload(String engineName) {
        LOGGER.info("start reload engine {}", engineName);
    }

    @Override
    public void loadCompleted(String engineName, Throwable e) {
        if (e == null) {
            LOGGER.info("reload engine {} successfully completed", engineName);
        } else {
            LOGGER.warn("reload engine {} failed", engineName, e);
        }
    }
}
