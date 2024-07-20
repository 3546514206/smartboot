package org.smartboot.flow.core.attribute;

/**
 * @author qinluo
 * @date 2022-11-14 19:02:20
 * @since 1.0.0
 */
public class AttributeHolder {

    private final Attributes attribute;
    private Object value;

    private AttributeHolder(Attributes attribute, Object value) {
        this.attribute = attribute;
        this.value = value;
    }

    public static AttributeHolder of(Attributes attribute, Object value) {
        return new AttributeHolder(attribute, value);
    }

    public Attributes getAttribute() {
        return attribute;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
