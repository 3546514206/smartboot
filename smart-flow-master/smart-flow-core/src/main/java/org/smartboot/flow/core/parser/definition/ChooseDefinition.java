package org.smartboot.flow.core.parser.definition;

import org.smartboot.flow.core.component.ChooseComponent;
import org.smartboot.flow.core.parser.DefinitionVisitor;
import org.smartboot.flow.core.util.AssertUtil;

import java.util.Map;

/**
 * @author huqiang
 * @since 2022/11/16 15:29
 */
public class ChooseDefinition extends ConditionDefinition {

    private ComponentDefinition defaultDef;

    private Map<String, ComponentDefinition> caseMap;

    public ComponentDefinition getDefaultDef() {
        return this.defaultDef;
    }

    public void setDefaultDef(ComponentDefinition chooseDefaultRef) {
        this.defaultDef = chooseDefaultRef;
    }

    public Map<String, ComponentDefinition> getCaseMap() {
        return this.caseMap;
    }

    public void setCaseMap(Map<String, ComponentDefinition> caseMap) {
        this.caseMap = caseMap;
    }

    @Override
    public void validate() {
        AssertUtil.notNull(caseMap, "choose branch is empty");
        AssertUtil.isTrue(caseMap.size() > 0, "choose branch is empty");
        caseMap.forEach((k ,v) -> v.validate());

        if (defaultDef != null) {
            defaultDef.validate();
        }
    }

    @Override
    public void doVisit(DefinitionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Class<?> resolveType() {
        return ChooseComponent.class;
    }
}
