package org.smartboot.flow.manager.reload;

import org.smartboot.flow.core.exception.FlowException;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.util.AuxiliaryUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author qinluo
 * @date 2023-01-04 11:55:50
 * @since 1.0.0
 */
public class SqlXmlSelector implements XmlSelector {

    /**
     * <code>classpath*:/resources/init.sql</code>
     */
    private final static String SELECT_SQL = "select content from engine_table where engine_name = ? and status = 0 limit 1";
    private String url;
    private String username;
    private String password;
    private String driver = "com.mysql.jdbc.Driver";
    private volatile boolean initialized;

    @Override
    public String select(String engineName) {
        init();

        try {
            Connection conn = this.openConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(SELECT_SQL);
            preparedStatement.setString(1, engineName);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            }

            return null;

        } catch (Exception e) {
            throw new FlowException("select failed", e);
        }
    }


    private synchronized void init() {
        if (initialized) {
            return;
        }

        AssertUtil.notBlank(driver, "driver class must not be null");
        AssertUtil.notBlank(url, "url class must not be null");
        AssertUtil.notNull(AuxiliaryUtils.asClass(driver), "driver " + driver + " not a class");

        this.openConnection();

        initialized = true;
    }

    private Connection openConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new FlowException("open connection failed", e);
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }
}
