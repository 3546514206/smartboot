package org.smartboot.flow.core.parser;

/**
 * @author qinluo
 * @date 2023-04-09 17:00:53
 * @since 1.0.0
 */
public interface AttributeAcceptor {

    /**
     * Check attribute is acceptable.
     *
     * @param attribute attribute.
     * @return          acceptable.
     */
    boolean accept(String attribute);
}
