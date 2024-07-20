package org.smartboot.smart.flow.admin.extension;

import org.smartboot.flow.core.parser.AttributeAcceptor;
import org.smartboot.flow.core.parser.ExtensionAttrParser;

/**
 * @author qinluo
 * @date 2023-04-09 17:38:43
 * @since 1.0.0
 */
public class AllExtensionAttributeParser implements ExtensionAttrParser {

    @Override
    public String prefix() {
        return "all-extension-attributes";
    }

    @Override
    public AttributeAcceptor acceptor() {
        // accept all attributes.
        return attribute -> true;
    }
}
