package org.smartboot.flow.core.parser.definition;

import org.smartboot.flow.core.component.PipelineComponent;
import org.smartboot.flow.core.parser.DefinitionVisitor;
import org.smartboot.flow.core.util.AssertUtil;

/**
 * @author qinluo
 * @date 2022-11-16 13:10:17
 * @since 1.0.0
 */
public class PipelineComponentDefinition extends ComponentDefinition {

    /**
     * pipeline name.
     */
    private String pipeline;

    public String getPipeline() {
        return pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public void validate() {
        super.validate();
        AssertUtil.notNull(pipeline, "inner pipeline's name must not be null");
        AssertUtil.notNull(context.getRegistered(pipeline), "pipeline " + pipeline + " not exist in " + name);
    }

    @Override
    public void doVisit(DefinitionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Class<?> resolveType() {
        return PipelineComponent.class;
    }
}
