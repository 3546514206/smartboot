package org.smartboot.flow.core.builder;

import org.smartboot.flow.core.parser.DefaultObjectCreator;
import org.smartboot.flow.core.parser.ObjectCreator;
import org.smartboot.flow.core.script.ScriptDetector;
import org.smartboot.flow.core.script.ScriptExecutor;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.util.AuxiliaryUtils;

/**
 *
 * @author qinluo
 * @date 2022-11-11 14:54:34
 * @since 1.0.9
 */
public class ScriptBuilder<T, S> {

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

    private ObjectCreator objectCreator = DefaultObjectCreator.getInstance();

    public ScriptBuilder<T, S> name(String name) {
        AssertUtil.notBlank(name, "name must not be blank");
        this.name = name;
        return this;
    }

    public ScriptBuilder<T, S> type(String type) {
        AssertUtil.notBlank(type, "type must not be blank");
        this.type = type;
        return this;
    }

    public ScriptBuilder<T, S> script(String script) {
        AssertUtil.notBlank(script, "script must not be blank");
        this.script = script;
        return this;
    }

    public ScriptBuilder<T, S> objectCreator(ObjectCreator creator) {
        AssertUtil.notNull(creator, "creator must not be blank");
        this.objectCreator = creator;
        return this;
    }

    public ScriptExecutor<T, S> build() {
        AssertUtil.notBlank(script, "script must not be blank");
        AssertUtil.notBlank(type, "type must not be blank");
        AssertUtil.notBlank(name, "name must not be blank");

        ScriptExecutor<T, S> executor = createExecutor(type);
        executor.setName(name);
        executor.setScript(script);
        return executor;
    }

    @SuppressWarnings("unchecked")
    private ScriptExecutor<T, S> createExecutor(String type) {
        Class<?> full = AuxiliaryUtils.asClass(type);
        if (full == null) {
            full = ScriptDetector.get().getJavaType(type);
        }
        return objectCreator.create(full != null ? full.getName() : type, ScriptExecutor.class, false);
    }
}
