package org.smartboot.plugin.resovler;

/**
 * @author qinluo
 * @date 2023-06-19 12:27:38
 * @since 1.1.0
 */
public class SystemPropertyResolver extends AbstractPropertyResolver {

    @Override
    public String getProperty(String name) {
        return System.getProperty(name);
    }
}
