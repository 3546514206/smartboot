package org.smartboot.flow.integration.nacos;

import com.alibaba.nacos.api.config.ConfigService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qinluo
 * @date 2023-08-09 10:44:55
 * @since 1.1.3
 */
public class NacosConfiguration {

    protected ConfigService configService;

    protected String dataId;
    protected String engine;
    protected String groupId = "DEFAULT_GROUP";
    protected Map<String, Object> nacosProperties = new HashMap<>();

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Map<String, Object> getNacosProperties() {
        return nacosProperties;
    }

    public void setNacosProperties(Map<String, Object> nacosProperties) {
        this.nacosProperties = nacosProperties;
    }

    public ConfigService getConfigService() {
        return configService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }
}
