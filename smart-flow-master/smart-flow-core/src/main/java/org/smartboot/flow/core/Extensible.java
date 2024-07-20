package org.smartboot.flow.core;

import java.util.List;

/**
 * @author qinluo
 * @date 2023-03-30 21:38:24
 * @since 1.0.9
 */
public interface Extensible {

    /**
     * Get extension values which prefixed prefix.
     *
     * @param prefix prefix
     * @return       value
     */
    ExtensionAttribute get(String prefix);

    /**
     * Get extension value which prefixed.
     *
     * @param prefix prefix
     * @return       value
     */
    String getValue(String prefix);

    /**
     * Get extension value with prefix and name.
     *
     * @param prefix prefix.
     * @param name   name.
     * @return       value.
     */
    String getValue(String prefix, String name);

    /**
     * Get All extension values.
     *
     * @return all.
     */
    List<ExtensionAttribute> get();
}
