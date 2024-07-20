package org.smartboot.flow.core.parser.definition;

import org.smartboot.flow.core.component.AdapterComponent;
import org.smartboot.flow.core.parser.DefinitionVisitor;
import org.smartboot.flow.core.util.AssertUtil;

/**
 * 适配器定义
 *
 * @author huqiang
 * @since 2022/12/8 10:58
 */
public class AdapterDefinition extends ElementDefinition {

    private ComponentDefinition pipelineElement;

    public ComponentDefinition getPipelineElement() {
        return this.pipelineElement;
    }

    public void setPipelineElement(ComponentDefinition pipelineElement) {
        this.pipelineElement = pipelineElement;
    }

    @Override
    public void validate() {
        AssertUtil.notBlank(getExecute(), " attribute[execute] must not be null");
        AssertUtil.notNull(pipelineElement, "pipeline children must not be empty");
    }

    @Override
    public Class<?> resolveType() {
        return AdapterComponent.class;
    }

    @Override
    public void doVisit(DefinitionVisitor visitor) {
        visitor.visit(this);
    }
}
