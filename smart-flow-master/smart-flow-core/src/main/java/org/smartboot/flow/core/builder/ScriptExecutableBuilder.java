package org.smartboot.flow.core.builder;

import org.smartboot.flow.core.script.ScriptExecutable;
import org.smartboot.flow.core.script.ScriptExecutor;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.util.AuxiliaryUtils;

/**
 * @author qinluo
 * @date 2023-03-30 23:50:55
 * @since 1.0.0
 */
public class ScriptExecutableBuilder<T, S> {

    /**
     * Script's name, required.
     */
    private String name;

    /**
     * Script's type, required.
     *
     * @see org.smartboot.flow.core.script.ScriptDetector#getJavaType(String)
     */
    private String type;

    /**
     * Script content, required.
     */
    private String script;

    /**
     * Rollback script, optional.
     */
    private String rollbackScript;

    /**
     * Rollback script type, null as {@link ScriptExecutableBuilder#type}
     */
    private String rollbackType;

    public ScriptExecutableBuilder<T, S> name(String name) {
        AssertUtil.notBlank(name, "name must not be blank");
        this.name = name;
        return this;
    }

    public ScriptExecutableBuilder<T, S> type(String type) {
        AssertUtil.notBlank(type, "type must not be blank");
        this.type = type;
        return this;
    }

    public ScriptExecutableBuilder<T, S> script(String script) {
        AssertUtil.notBlank(script, "script must not be blank");
        this.script = script;
        return this;
    }

    public ScriptExecutableBuilder<T, S> rollbackScript(String script) {
        AssertUtil.notBlank(script, "script must not be blank");
        this.rollbackScript = script;
        return this;
    }

    public ScriptExecutableBuilder<T, S> rollbackType(String rollbackType) {
        AssertUtil.notBlank(rollbackType, "rollbackType must not be blank");
        this.rollbackType = rollbackType;
        return this;
    }

    public ScriptExecutable<T, S> build() {
        AssertUtil.notBlank(script, "script must not be blank");
        AssertUtil.notBlank(type, "type must not be blank");
        AssertUtil.notBlank(name, "name must not be blank");

        ScriptExecutor<T, S> rollback = null;
        ScriptExecutor<T, S> executor = Builders.<T, S>script().name(name)
                                                .type(type).script(script).build();

        if (AuxiliaryUtils.isNotBlank(rollbackScript)) {
            rollback = Builders.<T, S>script().name(name + "-rollback")
                               .type(AuxiliaryUtils.or(rollbackType, type)).script(rollbackScript).build();
        }

        ScriptExecutable<T, S> executable = new ScriptExecutable<>();
        executable.setScriptExecutor(executor);
        executable.setRollbackExecutor(rollback);
        return executable;
    }
}
