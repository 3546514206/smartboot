package org.smartboot.smart.flow.admin.g6;

import java.io.Serializable;
import java.util.List;

/**
 * @author qinluo
 * @date 2023/2/18 21:43
 * @since 1.0.0
 */
public class ComponentInfo implements Serializable {
    private static final long serialVersionUID = -138563636659108643L;

    private String name;
    private String type;
    private String typeDesc;
    private String describe;

    private String scriptType;
    private String scriptName;
    private String script;

    private String rollbackScriptName;
    private String rollbackScriptType;
    private String rollbackScript;

    /**
     * 属性集合
     */
    private List<AttrInfo> attributes;

    private String extensionAttributes;

    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public List<AttrInfo> getAttributes() {
        return attributes;
    }

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String scriptType) {
        this.scriptType = scriptType;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public void setAttributes(List<AttrInfo> attributes) {
        this.attributes = attributes;
    }

    public String getRollbackScriptName() {
        return rollbackScriptName;
    }

    public void setRollbackScriptName(String rollbackScriptName) {
        this.rollbackScriptName = rollbackScriptName;
    }

    public String getRollbackScriptType() {
        return rollbackScriptType;
    }

    public void setRollbackScriptType(String rollbackScriptType) {
        this.rollbackScriptType = rollbackScriptType;
    }

    public String getRollbackScript() {
        return rollbackScript;
    }

    public void setRollbackScript(String rollbackScript) {
        this.rollbackScript = rollbackScript;
    }

    public void setExtensionAttributes(String extensionAttributes) {
        this.extensionAttributes = extensionAttributes;
    }

    public String getExtensionAttributes() {
        return extensionAttributes;
    }
}
