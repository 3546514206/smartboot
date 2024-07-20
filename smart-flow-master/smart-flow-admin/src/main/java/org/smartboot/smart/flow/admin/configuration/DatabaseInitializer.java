package org.smartboot.smart.flow.admin.configuration;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;

/**
 * @author qinluo
 * @date 2023-01-30
 * @since 1.0.0
 */
@Component
public class DatabaseInitializer implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private DatasourceConfiguration configuration;

    @Override
    public void afterPropertiesSet() throws Exception {
        InputStream schemaStream = this.getClass().getClassLoader().getResourceAsStream("schema.sql");
        if (schemaStream == null) {
            LOGGER.warn("can not find schema file [schema.sql] in class path");
            return;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;

        while ((len = schemaStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }

        String sql = bos.toString("UTF-8");
        if (sql == null || sql.trim().length() == 0) {
            LOGGER.warn("schema file [schema.sql] content is empty");
            return;
        }

        if (configuration.isH2Database()) {
            sql = "SET MODE MySQL;\n" + sql;
        }

        SqlSession sqlSession = sqlSessionFactory.openSession();
        Connection connection = sqlSession.getConnection();

        Statement statement = connection.createStatement();
        int execute = statement.executeUpdate(sql);

        LOGGER.warn("init schema sql result {}", execute);

        connection.commit();
        connection.close();
    }
}
