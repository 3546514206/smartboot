package org.smartboot.flow.spring.extension;

import org.smartboot.flow.core.exception.FlowException;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.manager.NullReloader;
import org.smartboot.flow.manager.reload.Reloader;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qinluo
 * @date 2023-01-05 19:42:58
 * @since 1.0.0
 */
public class EngineInitializer implements BeanPostProcessor {

    /**
     * Engine reloaders.
     */
    private final Map<String, Reloader> engines = new ConcurrentHashMap<>();

    /**
     * Default reloader is null.
     */
    private Reloader defaultReloader = NullReloader.NULL;

    @PostConstruct
    public void start() {
        for (Map.Entry<String, Reloader> entry : engines.entrySet()) {
            Reloader reloader = entry.getValue();
            reloader = (reloader != null && reloader != NullReloader.NULL) ? reloader : defaultReloader;

            try {
                reloader.reload(entry.getKey());
            } catch (Exception e) {
                if (e instanceof FlowException) {
                    throw (FlowException)e;
                }

                throw new FlowException(e);
            }
        }
    }


    public Reloader getReloader() {
        return defaultReloader;
    }

    public void setReloader(Reloader defaultReloader) {
        AssertUtil.notNull(defaultReloader, "reloader must not be null!");
        this.defaultReloader = defaultReloader;
    }

    public void setEngineReloaders(Map<String, Reloader> reloaderMap) {
        if (reloaderMap == null || reloaderMap.isEmpty()) {
            return;
        }

        reloaderMap.forEach((k, v) -> {
            Reloader nonNull = v != null ? v : defaultReloader;
            engines.put(k, nonNull);
        });
    }

    public void setEngines(List<String> engines) {
        if (engines == null || engines.isEmpty()) {
            return;
        }

        engines.forEach((k) -> this.engines.put(k, defaultReloader));
    }
}
