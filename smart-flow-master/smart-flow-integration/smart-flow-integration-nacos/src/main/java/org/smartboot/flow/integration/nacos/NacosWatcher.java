package org.smartboot.flow.integration.nacos;

import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.util.BeanUtils;
import org.smartboot.flow.manager.reload.MemoryXmlSelector;
import org.smartboot.flow.manager.reload.ReloadWatcher;
import org.smartboot.flow.manager.reload.Reloader;
import org.smartboot.flow.manager.reload.XmlParseReloader;

import javax.annotation.PostConstruct;

/**
 * @author qinluo
 * @date 2023-08-09 10:44:55
 * @since 1.1.3
 */
public class NacosWatcher extends NacosConfiguration implements ReloadWatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(NacosWatcher.class);

    private Reloader reloader = new XmlParseReloader();

    private NacosXmlSelector notifier;

    /**
     * For reflection.
     */
    public NacosWatcher() {
    }

    public NacosWatcher(NacosXmlSelector notifier) {
        this.notifier = notifier;
    }

    private void init() {
        if (configService == null) {
            configService = NacosFlowUtil.createCfgService(nacosProperties);
            AssertUtil.notNull(configService, "nacos config service must not be null!");
        }

        if (reloader == null) {
            reloader = BeanUtils.getBean(Reloader.class);
        }

        if (reloader == null) {
            XmlParseReloader r = new XmlParseReloader();
            r.setAssemble(true);
            reloader = r;
        }
    }

    @PostConstruct
    public void start() {
        init();

        try {
            configService.addListener(dataId, groupId, new AbstractListener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    if (notifier != null) {
                        notifier.notifyContentReceived();
                    }

                    MemoryXmlSelector.updateContent(engine, configInfo);
                    reloader.reload(engine);
                }
            });
        } catch (NacosException e) {
            LOGGER.error("add nacos watcher failed, groupId = {}, dataId = {}, engine = {}", groupId, dataId, engine, e);
        }

    }

    public Reloader getReloader() {
        return reloader;
    }

    public void setReloader(Reloader reloader) {
        this.reloader = reloader;
    }
}
