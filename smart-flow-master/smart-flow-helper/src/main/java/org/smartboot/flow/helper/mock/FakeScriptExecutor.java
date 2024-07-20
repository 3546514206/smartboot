package org.smartboot.flow.helper.mock;

import org.smartboot.flow.core.EngineContext;
import org.smartboot.flow.core.script.ScriptDetector;
import org.smartboot.flow.core.script.ScriptExecutor;
import org.smartboot.flow.core.util.AuxiliaryUtils;

/**
 * Fake Class, do-nothing
 *
 * @author qinluo
 * @date 2023/1/27 12:35
 * @since 1.0.0
 */
public class FakeScriptExecutor extends ScriptExecutor<Object, Object> {

    private final String type;

    public FakeScriptExecutor(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        Class<?> javaType = AuxiliaryUtils.asClass(type);
        if (javaType != null) {
            return AuxiliaryUtils.or(ScriptDetector.get().getPhrase(javaType), type);
        }

        return type;
    }

    @Override
    public Object execute(EngineContext<Object, Object> context) {
        // do-noting
        return null;
    }

    @Override
    public String describe() {
        return type;
    }
}
