package org.smartboot.flow.core.script;

import org.smartboot.flow.core.EngineContext;
import org.smartboot.flow.core.NamedCondition;

/**
 * @author qinluo
 * @date 2022/11/28 19:41
 * @since 1.0.0
 */
public class ScriptCondition<T, S> extends NamedCondition<T, S> {

    private ScriptExecutor<T, S> scriptExecutor;

    public ScriptExecutor<T, S> getScriptExecutor() {
        return scriptExecutor;
    }

    public void setScriptExecutor(ScriptExecutor<T, S> scriptExecutor) {
        this.scriptExecutor = scriptExecutor;
    }

    /**
     * 执行条件表达式
     *
     * @param context 当前执行上下文
     * @return        执行结果
     */
    @Override
    public Object test(EngineContext<T, S> context) {
        return scriptExecutor.execute(context);
    }

    @Override
    public String describe() {
        return scriptExecutor.describe();
    }
}
