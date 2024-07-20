package org.smartboot.smart.flow.admin.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.smart.flow.admin.mapper.EngineMetricMapper;
import org.smartboot.smart.flow.admin.mapper.EngineTraceMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yamikaze
 * @date 2023/7/3 20:39
 * @since 1.0.0
 */
@Component
public class DataClear implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataClear.class);

    @Autowired
    private DatasourceConfiguration configuration;

    private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(2);

    @Autowired
    private EngineMetricMapper metricMapper;

    @Autowired
    private EngineTraceMapper traceMapper;

    @Value("${data.period.day:7}")
    private Integer day;

    @Override
    public void afterPropertiesSet() {
        if (!configuration.isH2Database()) {
            return;
        }

        executor.scheduleAtFixedRate(() -> {
            long current = System.currentTimeMillis();
            Date lastest = new Date(current - day * 24 * 60 * 60 * 1000);
            long deleted = metricMapper.deleteOldest(lastest);
            LOGGER.warn("deleted engine metrics oldest data {} from {}", deleted, lastest);
        }, 0, 1, TimeUnit.MINUTES);

        executor.scheduleAtFixedRate(() -> {
            long current = System.currentTimeMillis();
            Date lastest = new Date(current - day * 24 * 60 * 60 * 1000);
            long deleted = traceMapper.deleteOldest(lastest);
            LOGGER.warn("deleted engine trace oldest data {} from {}", deleted, lastest);
        }, 0, 1, TimeUnit.MINUTES);
    }
}
