package org.smartboot.flow.core.parser;

/**
 * @author qinluo
 * @date 2023-04-09 17:01:53
 * @since 1.0.0
 */
public class PrefixAttributeAcceptor implements AttributeAcceptor {

    private final String prefix;

    public PrefixAttributeAcceptor(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean accept(String attribute) {
        return attribute != null && attribute.startsWith(prefix);
    }
}
