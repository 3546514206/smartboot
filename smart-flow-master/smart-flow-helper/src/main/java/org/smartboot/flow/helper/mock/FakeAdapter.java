package org.smartboot.flow.helper.mock;

import org.smartboot.flow.core.Adapter;
import org.smartboot.flow.core.EngineContext;
import org.smartboot.flow.core.common.Pair;

/**
 * Fake Class, do-nothing
 *
 * @author qinluo
 * @date 2023/1/27 12:35
 * @since 1.0.0
 */
public class FakeAdapter implements Adapter<Object, Object, Object, Object> {

    private final String type;

    public FakeAdapter(String type) {
        this.type = type;
    }

    @Override
    public Pair<Object, Object> before(EngineContext<Object, Object> context) {
        return Pair.of(context.getReq(), context.getResult());
    }

    @Override
    public void after(EngineContext<Object, Object> origin, EngineContext<Object, Object> newContext) {

    }

    @Override
    public String describe() {
        return type;
    }
}
