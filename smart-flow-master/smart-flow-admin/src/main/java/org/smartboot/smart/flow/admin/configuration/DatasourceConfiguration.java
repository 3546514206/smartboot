package org.smartboot.smart.flow.admin.configuration;

import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.h2.jdbcx.JdbcDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * @author qinluo
 * @date 2023-01-30
 * @since 1.0.0
 */
@Configuration
public class DatasourceConfiguration {

    private boolean isH2;

    public boolean isH2Database() {
        return isH2;
    }

    @Primary
    @Bean("h2Datasource")
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource h2Datasource() {
        isH2 = true;
        DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setType(JdbcDataSource.class);
        dataSourceProperties.setUrl("jdbc:h2:mem:smart-flow;DB_CLOSE_DELAY=-1");
        dataSourceProperties.setUsername("root");
        dataSourceProperties.setPassword("admin123456");
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean
    public SqlSessionFactory getSqlSessionFactory(DataSource dataSource) throws Exception {

        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setDefaultScriptingLanguage(XMLLanguageDriver.class);
        sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/*.xml"));
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        sqlSessionFactory.setConfiguration(configuration);
        return sqlSessionFactory.getObject();
    }
}
