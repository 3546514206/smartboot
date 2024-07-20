package org.smartboot.flow.core.builder;

import org.smartboot.flow.core.Pipeline;
import org.smartboot.flow.core.component.Component;
import org.smartboot.flow.core.executable.Executable;
import org.smartboot.flow.core.executable.ExecutableAdapter;
import org.smartboot.flow.core.util.AssertUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qinluo
 * @date 2022-11-11 16:18:38
 * @since 1.0.0
 */
public class PipelineBuilder<T, S> {

    private String name;
    private final List<Component<T, S>> components = new ArrayList<>();

    public PipelineBuilder<T, S> name(String name) {
        AssertUtil.notBlank(name, "must not be null");
        this.name = name;
        return this;
    }

    public PipelineBuilder<T, S> next(Component<T, S> component) {
        AssertUtil.notNull(component, "must not be null");
        this.components.add(component);
        return this;
    }

    public PipelineBuilder<T, S> next(Executable<T, S> executable) {
        AssertUtil.notNull(executable, "must not be null");
        Component<T, S> adapter = new ExecutableAdapter<>(executable);
        this.components.add(adapter);
        return this;
    }

    public Pipeline<T, S> build() {
        AssertUtil.notBlank(name, "pipeline's name is required");
        AssertUtil.isTrue(components.size() > 0, "pipeline's components size must greater than zero");
        Pipeline<T, S> pipeline = new Pipeline<>();
        pipeline.setComponents(this.components);
        pipeline.setName(name);
        return pipeline;
    }

}
