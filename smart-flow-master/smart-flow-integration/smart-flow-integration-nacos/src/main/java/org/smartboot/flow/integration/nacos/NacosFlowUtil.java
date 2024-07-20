package org.smartboot.flow.integration.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.spring.beans.factory.annotation.ConfigServiceBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.util.BeanUtils;

import java.util.Map;
import java.util.Properties;

/**
 * @author qinluo
 * @date 2023-08-09 14:09:03
 * @since 1.1.3
 */
class NacosFlowUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(NacosFlowUtil.class);

    static ConfigService createCfgService(Map<String, Object> properties) {
        // 检查从bean上下文中是否能获取到configServiceFactory.
        ConfigService cfgService = createWithBeanCtx(properties);

        try {
            if (cfgService == null) {
                Properties prop = new Properties();
                prop.putAll(properties);
                return NacosFactory.createConfigService(prop);
            }
        } catch (Exception e) {
            LOGGER.error("create nacos config service failed, properties = {}", properties, e);
        }

        return cfgService;
    }


    private static ConfigService createWithBeanCtx(Map<String, Object> properties) {

        try {
            ConfigServiceBeanBuilder beanBuilder = BeanUtils.getBean(ConfigServiceBeanBuilder.BEAN_NAME);
            return beanBuilder.build(properties);
        } catch (Throwable ignored) {

        }

        return null;
    }
}
