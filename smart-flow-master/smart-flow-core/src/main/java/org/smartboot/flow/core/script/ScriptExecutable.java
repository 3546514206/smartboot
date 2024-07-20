package org.smartboot.flow.core.script;

import org.smartboot.flow.core.EngineContext;
import org.smartboot.flow.core.executable.AbstractExecutable;

/**
 * @author qinluo
 * @date 2023-02-27
 * @since 1.0.8
 */
public class ScriptExecutable<T, S> extends AbstractExecutable<T, S> {

    private ScriptExecutor<T, S> scriptExecutor;
    private ScriptExecutor<T, S> rollbackExecutor;

    public ScriptExecutor<T, S> getScriptExecutor() {
        return scriptExecutor;
    }

    public void setScriptExecutor(ScriptExecutor<T, S> scriptExecutor) {
        this.scriptExecutor = scriptExecutor;
    }

    public ScriptExecutor<T, S> getRollbackExecutor() {
        return rollbackExecutor;
    }

    public void setRollbackExecutor(ScriptExecutor<T, S> rollbackExecutor) {
        this.rollbackExecutor = rollbackExecutor;
    }

    @Override
    public void execute(EngineContext<T, S> ctx) {
        scriptExecutor.execute(ctx);
    }

    @Override
    public void rollback(EngineContext<T, S> ctx) {
        if (this.rollbackExecutor != null) {
            this.rollbackExecutor.execute(ctx);
        }
    }

    @Override
    public String describe() {
        return scriptExecutor.describe();
    }
}
