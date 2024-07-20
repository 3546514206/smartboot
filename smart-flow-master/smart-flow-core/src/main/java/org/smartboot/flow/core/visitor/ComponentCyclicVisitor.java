package org.smartboot.flow.core.visitor;

import org.smartboot.flow.core.common.ComponentType;
import org.smartboot.flow.core.component.Component;

import java.util.Set;

/**
 * @author huqiang
 * @since 2023/3/1 19:55
 */
public class ComponentCyclicVisitor extends ComponentVisitor {

    /**
     * 记录访问过的对象
     */
    private final Set<Object> visited;

    public ComponentCyclicVisitor(Set<Object> visited, ComponentVisitor componentVisitor) {
        super(componentVisitor);
        this.visited = visited;
    }

    @Override
    public <T, S> void visitSource(Component<T, S> component) {
        this.visited.add(component);
        super.visitSource(component);
    }

    @Override
    public PipelineVisitor visitPipeline(String pipeline) {
        PipelineVisitor pipelineVisitor = super.visitPipeline(pipeline);
        return new PipelineCyclicVisitor(visited, pipelineVisitor);
    }


    @Override
    public ComponentVisitor visitComponent(ComponentType type, String name, String describe) {
        ComponentVisitor componentVisitor = super.visitComponent(type, name, describe);
        return new ComponentCyclicVisitor(visited, componentVisitor);
    }


    @Override
    public ComponentVisitor visitBranch(Object branch, ComponentType type, String name, String describe) {
        ComponentVisitor componentVisitor = super.visitBranch(branch, type, name, describe);
        return new ComponentCyclicVisitor(visited, componentVisitor);
    }
}
