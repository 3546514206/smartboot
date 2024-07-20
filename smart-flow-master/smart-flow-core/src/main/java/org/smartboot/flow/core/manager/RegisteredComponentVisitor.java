package org.smartboot.flow.core.manager;

import org.smartboot.flow.core.attribute.AttributeHolder;
import org.smartboot.flow.core.common.ComponentType;
import org.smartboot.flow.core.component.Component;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.smartboot.flow.core.visitor.ComponentVisitor;
import org.smartboot.flow.core.visitor.PipelineVisitor;

import java.util.List;
import java.util.Set;

/**
 * @author qinluo
 * @date 2022/11/19 12:27
 * @since 1.0.0
 */
public class RegisteredComponentVisitor extends ComponentVisitor {

    private final ComponentModel model;
    private final Set<Object> visited;
    private final IdentifierAllocator allocator;

    public RegisteredComponentVisitor(ComponentModel comp, Set<Object> visited, IdentifierAllocator allocator) {
        this.model = comp;
        this.visited = visited;
        this.allocator = allocator;
    }

    @Override
    public PipelineVisitor visitPipeline(String pipeline) {
        // Re-allocate name.
        if (pipeline == null || AuxiliaryUtils.isAnonymous(pipeline)) {
            pipeline = allocator.allocate("pipeline");
        }

        PipelineModel pipelineModel = new PipelineModel(pipeline, pipeline);
        this.model.pipeline = pipelineModel;
        return new RegisteredPipelineVisitor(pipelineModel, visited, allocator);
    }

    @Override
    public <T, S> void visitSource(Component<T, S> component) {
        this.visited.add(component);
        this.model.setComponent(component);
    }

    @Override
    public ComponentVisitor visitComponent(ComponentType type, String name, String describe) {
        // Re-allocate name.
        if (name == null || AuxiliaryUtils.isAnonymous(name)) {
            name = allocator.allocate(type.name());
        }

        ComponentModel comp = new ComponentModel(type, name);
        this.model.addComponent(comp);
        return new RegisteredComponentVisitor(comp, visited, allocator);
    }

    @Override
    public ComponentVisitor visitBranch(Object branch, ComponentType type, String name, String describe) {
        // Re-allocate name.
        if (name == null || AuxiliaryUtils.isAnonymous(name)) {
            name = allocator.allocate(type.name());
        }

        ComponentModel model = new ComponentModel(type, name);
        this.model.addComponent(model);
        return new RegisteredComponentVisitor(model, visited, allocator);
    }

    @Override
    public void visitAttributes(List<AttributeHolder> attributes) {
        this.model.setHolders(attributes);
    }
}
