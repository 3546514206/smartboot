package org.smartboot.flow.helper.mock;

import org.smartboot.flow.core.Adapter;
import org.smartboot.flow.core.Condition;
import org.smartboot.flow.core.DegradeCallback;
import org.smartboot.flow.core.executable.Executable;
import org.smartboot.flow.core.parser.DefaultObjectCreator;
import org.smartboot.flow.core.parser.ObjectCreator;
import org.smartboot.flow.core.script.ScriptExecutor;

/**
 * Fake object creator, Return prepared fake object.
 *
 * @author qinluo
 * @date 2023/1/27 12:20
 * @since 1.0.5
 */
public class FakeObjectCreator implements ObjectCreator {

    @Override
    public <T> T create(String type, Class<T> expectType, boolean useCache) {
        // Created by DefaultObjectCreator.
        if (expectType == null) {
            return DefaultObjectCreator.getInstance().create(type, null, useCache);
        }

        if (expectType == Condition.class) {
            return (T)new FakeCondition(type);
        } else if (expectType == ScriptExecutor.class) {
            return (T)new FakeScriptExecutor(type);
        } else if (expectType == Executable.class) {
            return (T)new FakeExecutable(type);
        } else if (expectType == Adapter.class) {
            return (T)new FakeAdapter(type);
        } else if (expectType == DegradeCallback.class) {
            return (T)new FakeDegradeCallback(type);
        }

        // Default Created by DefaultObjectCreator.
        return DefaultObjectCreator.getInstance().create(type, expectType, useCache);
    }
}
