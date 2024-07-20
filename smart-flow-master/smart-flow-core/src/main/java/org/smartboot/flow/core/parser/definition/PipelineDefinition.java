package org.smartboot.flow.core.parser.definition;

import org.smartboot.flow.core.Pipeline;
import org.smartboot.flow.core.parser.DefinitionVisitor;
import org.smartboot.flow.core.util.AssertUtil;

import java.util.List;

/**
 * @author qinluo
 * @date 2022-11-16 12:40:46
 * @since 1.0.0
 */
public class PipelineDefinition extends FlowDefinition {

    private List<ComponentDefinition> children;

    public List<ComponentDefinition> getChildren() {
        return children;
    }

    public void setChildren(List<ComponentDefinition> children) {
        this.children = children;
    }

    @Override
    public void validate() {
        super.validate();
        AssertUtil.notNull(children, "pipeline children must not be empty");
        AssertUtil.isTrue(children.size() != 0, "pipeline children must not be empty");
    }

    @Override
    public void doVisit(DefinitionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Class<?> resolveType() {
        return Pipeline.class;
    }
}
