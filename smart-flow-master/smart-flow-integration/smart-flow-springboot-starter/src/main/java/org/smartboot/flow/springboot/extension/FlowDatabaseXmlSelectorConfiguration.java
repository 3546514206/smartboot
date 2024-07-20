package org.smartboot.flow.springboot.extension;

import org.smartboot.flow.manager.reload.SqlXmlSelector;
import org.smartboot.flow.manager.reload.XmlSelector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * smart flow reload auto config
 *
 * @author huqiang
 * @since 2022/12/15 16:32
 */
@Configuration
@ConfigurationProperties(prefix = "smart.flow.selector")
@ConditionalOnProperty(name = "smart.flow.selector.datasource.url")
public class FlowDatabaseXmlSelectorConfiguration {

    /**
     * Selector datasource config.
     */
    private DataSource datasource;

    /**
     * Selector datasource.
     */
    public static class DataSource {
        private String url;

        /**
         * Ensure driver lib in classpath.
         */
        private String driver;
        private String username;
        private String password;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDriver() {
            return driver;
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    @Bean("databaseXmlSelector")
    public XmlSelector getXmlSelector() {
        SqlXmlSelector selector = new SqlXmlSelector();

        if (this.datasource.getDriver() != null) {
            selector.setDriver(datasource.getDriver());
        }

        selector.setUrl(datasource.getUrl());
        selector.setUsername(datasource.getUsername());
        selector.setPassword(datasource.getPassword());
        return selector;
    }

    public void setDatasource(DataSource dataSource) {
        this.datasource = dataSource;
    }
}
