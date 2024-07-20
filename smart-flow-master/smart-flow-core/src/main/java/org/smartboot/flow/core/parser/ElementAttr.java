package org.smartboot.flow.core.parser;

import java.io.Serializable;

/**
 * @author qinluo
 * @date 2023/3/19 13:52
 * @since 1.0.0
 */
public class ElementAttr implements Serializable {

    private static final long serialVersionUID = 2105126904710511899L;

    private final String name;
    private final String value;

    private ElementAttr(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static ElementAttr of(String name, String value) {
        return new ElementAttr(name, value);
    }
}
