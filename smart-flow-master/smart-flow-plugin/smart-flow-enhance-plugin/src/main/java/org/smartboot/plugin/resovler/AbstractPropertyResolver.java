package org.smartboot.plugin.resovler;

/**
 * @author qinluo
 * @date 2023-06-19 12:19:46
 * @since 1.1.0
 */
public abstract class AbstractPropertyResolver {

    /**
     * Get named property
     *
     * @param name name.
     * @return     named property
     */
    public abstract String getProperty(String name);
}
