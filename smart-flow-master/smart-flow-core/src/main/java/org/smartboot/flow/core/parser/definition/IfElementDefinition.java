package org.smartboot.flow.core.parser.definition;

import org.smartboot.flow.core.component.IfComponent;
import org.smartboot.flow.core.parser.DefinitionVisitor;
import org.smartboot.flow.core.util.AssertUtil;

/**
 * @author huqiang
 * @since 2022/11/16 15:30
 */
public class IfElementDefinition extends ConditionDefinition {

    private ComponentDefinition ifThenRef;

    private ComponentDefinition ifElseRef;

    public ComponentDefinition getIfThenRef() {
        return this.ifThenRef;
    }

    public void setIfThenRef(ComponentDefinition ifThenRef) {
        this.ifThenRef = ifThenRef;
    }

    public ComponentDefinition getIfElseRef() {
        return this.ifElseRef;
    }

    public void setIfElseRef(ComponentDefinition ifElseRef) {
        this.ifElseRef = ifElseRef;
    }

    @Override
    public void validate() {
        super.validate();
        AssertUtil.notNull(ifThenRef, "if element named " + name + " lack then tag");

        ifThenRef.validate();
        if (ifElseRef != null) {
            ifElseRef.validate();
        }
    }

    @Override
    public void doVisit(DefinitionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Class<?> resolveType() {
        return IfComponent.class;
    }
}
