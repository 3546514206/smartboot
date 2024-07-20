package org.smartboot.flow.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qinluo
 * @date 2023-03-30 21:47:58
 * @since 1.0.9
 */
public abstract class AbstractExtensible implements Extensible {

    private Map<String, ExtensionAttribute> attributeMap = new HashMap<>(0);

    public void setAttributeMap(Map<String, ExtensionAttribute> attributeMap) {
        this.attributeMap = attributeMap;
    }

    @Override
    public ExtensionAttribute get(String prefix) {
        return attributeMap.get(prefix);
    }

    @Override
    public String getValue(String prefix) {
        ExtensionAttribute ea = get(prefix);
        return ea != null ? ea.getValues().get(prefix) : null;
    }

    @Override
    public String getValue(String prefix, String name) {
        ExtensionAttribute ea = get(prefix);
        if (ea != null) {
            return ea.getValues().get(prefix + "." + name);
        }

        return null;
    }

    @Override
    public List<ExtensionAttribute> get() {
        return new ArrayList<>(attributeMap.values());
    }
}
