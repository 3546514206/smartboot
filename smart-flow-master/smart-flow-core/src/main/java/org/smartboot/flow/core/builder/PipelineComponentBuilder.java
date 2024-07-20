package org.smartboot.flow.core.builder;


import org.smartboot.flow.core.Pipeline;
import org.smartboot.flow.core.attribute.AttributeHolder;
import org.smartboot.flow.core.attribute.AttributeValueResolver;
import org.smartboot.flow.core.attribute.Attributes;
import org.smartboot.flow.core.component.PipelineComponent;
import org.smartboot.flow.core.util.AssertUtil;

import java.util.List;

/**
 * @author qinluo
 * @date 2022-11-11 11:40:20
 * @since 1.0.9
 */
public class PipelineComponentBuilder<T, S> extends AbstractComponentBuilder<T, S>{

    /**
     * This component's condition
     */
    private Pipeline<T, S> pipeline;
    private boolean delayPipeline;

    @Override
    public PipelineComponentBuilder<T, S> apply(Attributes attributes, Object value) {
        super.apply(attributes, value);
        return this;
    }

    @Override
    public PipelineComponentBuilder<T, S> withResolver(AttributeValueResolver resolver) {
        super.withResolver(resolver);
        return this;
    }

    @Override
    public PipelineComponentBuilder<T, S> apply(List<AttributeHolder> holders) {
        super.apply(holders);
        return this;
    }

    public PipelineComponentBuilder<T, S> pipeline(Pipeline<T, S> pipeline) {
        this.pipeline = pipeline;
        return this;
    }

    public PipelineComponentBuilder<T, S> delayPipeline(boolean delay) {
        this.delayPipeline = delay;
        return this;
    }

    @Override
    public PipelineComponent<T, S> build() {
        if (!delayPipeline) {
            AssertUtil.notNull(pipeline, "pipeline must not be null");
        }

        PipelineComponent<T, S> component = new PipelineComponent<>();
        applyValues(component);
        component.setPipeline(pipeline);
        return component;
    }
}
