package org.smartboot.flow.manager.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.manager.EngineManager;
import org.smartboot.flow.core.manager.EngineModel;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.manager.NamedThreadFactory;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author qinluo
 * @date 2022/11/23 20:35
 * @since 1.0.0
 */
public abstract class AbstractReporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractReporter.class);
    private final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("export-thread"));

    /**
     * Report idle in mills.
     */
    protected long idle = 5000L;

    public long getIdle() {
        return idle;
    }

    public void setIdle(long idle) {
        AssertUtil.isTrue(idle > 0, "idle must great than zero");
        this.idle = idle;
    }

    final void export() {
        EngineManager defaultManager = EngineManager.defaultManager();
        List<String> registeredEngineNames = defaultManager.getRegisteredEngineNames();

        try {
            for (String name : registeredEngineNames) {
                this.doExport(defaultManager.getEngineModel(name));
            }
        } catch (Exception e) {
            LOGGER.error("{} export engine model failed.", getClass().getName(), e);
        }
    }

    /**
     * Start to export data.
     */
    public void start() {
        executorService.scheduleAtFixedRate(this::export, idle, idle, TimeUnit.MILLISECONDS);
    }


    /**
     * Export engine model
     *
     * @param model engine model.
     */
    public abstract void doExport(EngineModel model);

}
