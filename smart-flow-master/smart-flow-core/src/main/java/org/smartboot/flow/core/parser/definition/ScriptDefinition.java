package org.smartboot.flow.core.parser.definition;

import org.smartboot.flow.core.parser.DefinitionVisitor;
import org.smartboot.flow.core.script.ScriptDetector;
import org.smartboot.flow.core.script.ScriptExecutor;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.util.AuxiliaryUtils;

/**
 * @author qinluo
 * @date 2022-11-15 13:00:20
 * @since 1.0.0
 */
public class ScriptDefinition extends FlowDefinition {

    private String type;
    private String script;

    @Override
    public void validate() {
        AssertUtil.notBlank(name, "script name must not be blank");
        AssertUtil.notBlank(type, "script type must not be blank");
        AssertUtil.notBlank(script, "script must not be blank");

        Class<?> javaType;
        if (!AuxiliaryUtils.isType(type)) {
            javaType = ScriptDetector.get().getJavaType(type);
        } else {
            javaType = AuxiliaryUtils.asClass(type);
        }

        if (javaType != null) {
            AssertUtil.isTrue(ScriptExecutor.class.isAssignableFrom(javaType), "script type must be a subclass of ScriptExecutor");
            AssertUtil.isTrue(ScriptExecutor.class != javaType, "script type must be a subclass of ScriptExecutor");
        }
    }

    /**
     * Return script java type.
     *
     * @return maybe null in spring container.
     */
    public Class<?> getJavaType() {
        Class<?> javaType = AuxiliaryUtils.asClass(type);
        if (javaType == null) {
            javaType = ScriptDetector.get().getJavaType(type);
        }

        return javaType;
    }

    @Override
    public Class<?> resolveType() {
        return getJavaType();
    }

    @Override
    public void doVisit(DefinitionVisitor visitor) {
        visitor.visit(this);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
