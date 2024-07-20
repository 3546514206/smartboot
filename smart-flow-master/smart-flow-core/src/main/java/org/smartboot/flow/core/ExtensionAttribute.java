package org.smartboot.flow.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qinluo
 * @date 2023-03-30 21:40:24
 * @since 1.0.9
 */
public class ExtensionAttribute {

    private String prefix;
    private final Map<String, String> values = new HashMap<>(0);

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Map<String, String> getValues() {
        return values;
    }
}
