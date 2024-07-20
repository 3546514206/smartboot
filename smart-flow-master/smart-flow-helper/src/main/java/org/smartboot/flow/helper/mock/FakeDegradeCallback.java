
package org.smartboot.flow.helper.mock;

import org.smartboot.flow.core.DegradeCallback;

/**
 * Fake Class, do-nothing
 *
 * @author qinluo
 * @date 2023/1/27 12:35
 * @since 1.0.0
 */
public class FakeDegradeCallback implements DegradeCallback<Object, Object> {

    private final String type;

    public FakeDegradeCallback(String type) {
        this.type = type;
    }


    @Override
    public String describe() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }
}
