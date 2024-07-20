package org.smartboot.flow.core.parser.definition;

import org.smartboot.flow.core.ExtensionAttribute;
import org.smartboot.flow.core.attribute.AttributeHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yamikaze
 * @date 2023/6/17 10:33
 * @since 1.1.0
 */
public abstract class ComponentDefinition extends FlowDefinition {

    private final List<AttributeHolder> attributes = new ArrayList<>();
    private final Map<String, ExtensionAttribute> attributeMap = new HashMap<>(0);

    public List<AttributeHolder> getAttributes() {
        return attributes;
    }

    public Map<String, ExtensionAttribute> getAttributeMap() {
        return attributeMap;
    }
}
