package org.smartboot.flow.core.attribute;

import org.smartboot.flow.core.exception.FlowException;
import org.smartboot.flow.core.parser.DefaultObjectCreator;
import org.smartboot.flow.core.parser.ObjectCreator;
import org.smartboot.flow.core.util.AssertUtil;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * @author qinluo
 * @date 2022-11-28 20:41:17
 * @since 1.0.0
 */
public class AttributeValueResolver {

    private static final AttributeValueResolver INSTANCE = new AttributeValueResolver();
    private ObjectCreator objectCreator = DefaultObjectCreator.getInstance();

    public AttributeValueResolver() {
    }

    public AttributeValueResolver(ObjectCreator objectCreator) {
        this.objectCreator = objectCreator;
    }

    public void setObjectCreator(ObjectCreator objectCreator) {
        this.objectCreator = objectCreator;
    }

    public ObjectCreator getObjectCreator() {
        return objectCreator;
    }

    public static AttributeValueResolver getInstance() {
        return INSTANCE;
    }

    protected String earlyResolve(String value) {
        return value;
    }

    @SuppressWarnings("unchecked")
    public <T> T resolve(Class<T> accepted, Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            value = earlyResolve((String)value);
        }

        if (accepted.isInstance(value) && accepted != Object.class) {
            return (T)value;
        }

        // Must as string
        String strValue = String.valueOf(value);

        // For string
        if (accepted == String.class) {
            return (T)strValue;
        }

        // For boolean
        if (accepted == Boolean.class || accepted == boolean.class) {
            return (T)Boolean.valueOf(Boolean.parseBoolean(strValue));
        }

        if (accepted == Character.class || accepted == char.class) {
            return (T)new Character(strValue.charAt(0));
        }

        // For numbers
        if (Number.class.isAssignableFrom(accepted) || accepted.isPrimitive()) {
            Object resolved = null;
            double numeric = Double.parseDouble(strValue);
            if (accepted == Long.class || accepted == long.class) {
                resolved = (long)numeric;
            } else if (accepted == Double.class || accepted == double.class) {
                resolved = numeric;
            } else if (accepted == Integer.class || accepted == int.class) {
                resolved = (int) numeric;
            } else if (accepted == Float.class || accepted == float.class) {
                resolved = (float) numeric;
            } else if (accepted == Short.class || accepted == short.class) {
                resolved = (short) numeric;
            } else if (accepted == Byte.class || accepted == byte.class) {
                resolved = (byte) numeric;
            } else if (accepted == BigDecimal.class) {
                resolved = new BigDecimal(strValue);
            } else {
                AssertUtil.shouldNotReachHere();
            }

            return (T)resolved;
        }

        // For string list.
        if (accepted == List.class) {
            return (T)Arrays.asList(strValue.split(","));
        }

        // for classname.
        try {
            return objectCreator.create(strValue, accepted,false);
        } catch (Exception ignored) {
            // Maybe not a class.
        }

        throw new FlowException("Can't not resolve [" + accepted + "] value with " + value);
    }

    public Object resolve(Attributes attribute, Object value) {
        return resolve(attribute.accept, value);
    }
}
