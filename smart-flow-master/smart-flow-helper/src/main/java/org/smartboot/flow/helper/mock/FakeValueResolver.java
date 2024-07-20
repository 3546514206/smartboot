package org.smartboot.flow.helper.mock;

import org.smartboot.flow.core.attribute.AttributeValueResolver;
import org.smartboot.flow.core.attribute.Attributes;

/**
 * Fake value resolver
 *
 * @author qinluo
 * @date 2023/1/27 12:35
 * @since 1.1.3
 */
public class FakeValueResolver extends AttributeValueResolver {

    public FakeValueResolver() {
        this.setObjectCreator(new FakeObjectCreator());
    }

    @Override
    public Object resolve(Attributes attribute, Object value) {
        try {
            return super.resolve(attribute, value);
        } catch (Exception ignored) {

        }

        // Miss parsed, as default value.
        Class<?> accepted = attribute.getAccept();
        if (accepted == String.class) {
            return String.valueOf(value);
        } else if (accepted == Long.class) {
            return 0L;
        } else if (accepted == Boolean.class) {
            return false;
        } else {
            return null;
        }
    }
}
