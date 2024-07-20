package org.smartboot.flow.integration.nacos;

import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.manager.reload.ReloadWatcher;
import org.smartboot.flow.manager.reload.XmlSelector;

import javax.annotation.PostConstruct;

/**
 * @author qinluo
 * @date 2023-08-09 10:44:55
 * @since 1.1.3
 */
public class NacosXmlSelector extends NacosConfiguration implements XmlSelector, ReloadWatcher {

    private boolean enableWatcher;
    private boolean fetch = true;
    private volatile boolean initialized = false;
    private final NacosWatcher watcher = new NacosWatcher(this);
    private volatile boolean enabledWatcher = false;

    @PostConstruct
    public synchronized void init() {
        if (initialized) {
            return;
        }

        if (configService == null) {
            configService = NacosFlowUtil.createCfgService(nacosProperties);
            AssertUtil.notNull(configService, "nacos config service must not be null!");
        }

        watcher.setConfigService(configService);
        watcher.setEngine(engine);
        watcher.setGroupId(groupId);
        watcher.setNacosProperties(nacosProperties);
        watcher.setDataId(dataId);

        initialized = true;
    }

    @Override
    public String select(String engineName) {
        init();

        String content = null;

        try {
            if (fetch) {
                content = configService.getConfig(dataId, groupId, 1000);
            }

            if (enableWatcher && !enabledWatcher) {
                watcher.start();
                enabledWatcher = true;
            }
        } catch (Exception ignored) {

        }
        return content;
    }

    public boolean getEnableWatcher() {
        return enableWatcher;
    }

    public void setEnableWatcher(boolean enableWatcher) {
        this.enableWatcher = enableWatcher;
    }

    void notifyContentReceived() {
        this.fetch = false;
    }
}
