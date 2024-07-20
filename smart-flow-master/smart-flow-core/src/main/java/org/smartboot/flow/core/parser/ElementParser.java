package org.smartboot.flow.core.parser;

import org.w3c.dom.Element;

/**
 * @author qinluo
 * @date 2022-11-15 12:41:20
 * @since 1.0.0
 */
public interface ElementParser {

    /**
     * Parse element.
     *
     * @param element element.
     * @param context context;
     */
    void parseElement(Element element, ParserContext context);
}
