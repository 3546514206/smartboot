package org.smartboot.plugin.bootstrap;

import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.core.Validator;
import org.smartboot.flow.core.attribute.AttributeValueResolver;
import org.smartboot.flow.core.builder.Builders;
import org.smartboot.flow.core.builder.PipelineBuilder;
import org.smartboot.flow.core.util.AssertUtil;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * @author yamikaze
 * @date 2023/6/18 19:19
 * @since 1.0.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class YamlConfig implements Serializable, Validator {
    private static final long serialVersionUID = -3183221057461505827L;
    private String name;
    private String pipeline;
    private List<Step> steps;
    private Integer threads;
    private transient AttributeValueResolver resolver;

    @Override
    public void validate() {
        AssertUtil.notNull(name, "engine-name must not be null!");
        AssertUtil.notNull(pipeline, "pipeline must not be null!");
        AssertUtil.notNull(steps, "steps must not be null!");
    }

    public FlowEngine assemble() {
        threads = (threads != null) ? threads : Runtime.getRuntime().availableProcessors();

        PipelineBuilder pipelineBuilder = Builders.pipeline().name(pipeline);
        for (Step step : steps) {
            pipelineBuilder.next(step.assemble(resolver));
        }

        return Builders.engine().name(resolver.resolve(String.class, name))
                .executor(Executors.newFixedThreadPool(threads))
                .pipeline(pipelineBuilder.build()).build();
    }

    public AttributeValueResolver getResolver() {
        return resolver;
    }

    public void setResolver(AttributeValueResolver resolver) {
        this.resolver = resolver;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPipeline() {
        return pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }
}
