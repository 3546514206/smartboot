package org.smartboot.flow.spring.extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.core.manager.EngineManager;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.smartboot.flow.manager.reload.ReloadListener;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author qinluo
 * @date 2023/1/31 23:17
 * @since 1.0.0
 */
public class ReflectionNotifier implements ReloadListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionNotifier.class);

    private final BeanFactory factory;
    private final List<NotifyConf> notifiers = new CopyOnWriteArrayList<>();

    public ReflectionNotifier(BeanFactory factory) {
        this.factory = factory;
    }

    @Override
    public void onload(String engineName) {

    }

    @Override
    public void loadCompleted(String engineName, Throwable e) {
        if (e != null) {
            return;
        }

        FlowEngine<?, ?> engineInstance = null;
        FlowEngine<?, ?> springEngine = null;
        FlowEngine<?, ?> managerEngine = EngineManager.getEngine(engineName);
        try {
            springEngine = (FlowEngine<?, ?>)factory.getBean(engineName);
        } catch (Exception ex) {
            // do-nothing
        }

        if (springEngine == null ^ managerEngine == null) {
            engineInstance = AuxiliaryUtils.or(springEngine, managerEngine);
        } else if (springEngine != null) {
            if (springEngine.getStartedAt() >= managerEngine.getStartedAt()) {
                engineInstance = springEngine;
            } else if(springEngine.getStartedAt() < managerEngine.getStartedAt()){
                engineInstance = managerEngine;
            }
        }

        // Cannot find engine instance.
        if (engineInstance == null) {
            return;
        }

        for (NotifyConf conf : notifiers) {
            conf.notify(engineName, engineInstance);
        }

    }

    public void addNotifyField(Field field, Object bean, List<String> accepted) {
        notifiers.add(new NotifyConf(field, bean, accepted));
    }

    private static class NotifyConf {
        private final Field f;
        private final Object bean;
        private final List<String> accepted;
        private final boolean acceptAny;

        NotifyConf(Field f, Object bean, List<String> accepted) {
            this.f = f;
            this.bean = bean;
            this.accepted = accepted;
            this.acceptAny = accepted.size() == 1 && Objects.equals(accepted.get(0), "");
        }

        public void notify(String engineName, FlowEngine<?, ?> engineInstance) {
            if (!acceptAny && !accepted.contains(engineName)) {
                return;
            }

            if (FlowEngine.class.isAssignableFrom(f.getType())) {
                ReflectionUtils.setField(f, bean, engineInstance);
                LOGGER.info("reload-notify engine {}({}) to field {}#{}",
                        engineName, engineInstance.getStartedAt(), f.getDeclaringClass().getSimpleName(), f.getName());
            } else if ((Map.class.isAssignableFrom(f.getType()))) {
                Map<String, Object> engines = (Map<String, Object>)ReflectionUtils.getField(f, bean);
                if (engines == null) {
                    return;
                }

                Object o = engines.get(engineName);
                if (o instanceof FlowEngine) {
                    engines.put(engineName, engineInstance);

                    LOGGER.info("reload-notify engine {}({}) to field {}#{}",
                            engineName, engineInstance.getStartedAt(), f.getDeclaringClass().getSimpleName(), f.getName());
                }

            }


        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ReflectionNotifier;
    }

    @Override
    public int hashCode() {
        return ReflectionNotifier.class.hashCode();
    }
}
