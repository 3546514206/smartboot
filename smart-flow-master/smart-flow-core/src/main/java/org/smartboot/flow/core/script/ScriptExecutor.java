package org.smartboot.flow.core.script;

import org.smartboot.flow.core.Describable;
import org.smartboot.flow.core.EngineContext;

import java.util.Map;

/**
 * @author qinluo
 * @date 2023-02-27
 * @since 1.0.8
 */
public abstract class ScriptExecutor<T, S> implements Describable {

    protected String name;

    /**
     * 执行脚本
     */
    protected String script;

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取脚本条件的类型
     *
     * @return type string
     */
    public abstract String getType();

    /**
     * 允许用户在脚本上下文中绑定自定义的变量，用于执行脚本。
     * 其中key为脚本中用到的变量名，内置变量名参考 {@link org.smartboot.flow.core.script.ScriptConstants}
     *
     * @since 1.0.5
     * @param context engine ctx.
     * @return        bound keys.
     */
    protected Map<String, Object> bindCustomized(EngineContext<T, S> context) {
        return ScriptVariableManager.getRegistered(context.getEngineName());
    }

    /**
     * Execute script.
     *
     * @param ctx ctx.
     * @return    result.
     */
    public abstract Object execute(EngineContext<T, S> ctx);

    @Override
    public String describe() {
        return name + "@" + getType() + "-script";
    }
}
