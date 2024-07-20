package org.smartboot.flow.core.visitor;

import org.smartboot.flow.core.Pipeline;
import org.smartboot.flow.core.common.ComponentType;

import java.util.Set;

/**
 * @author huqiang
 * @since 2023/3/1 20:03
 */
public class PipelineCyclicVisitor extends PipelineVisitor {

    private final Set<Object> visited;

    private boolean cycle;

    public PipelineCyclicVisitor(Set<Object> visited, PipelineVisitor delegate) {
        super(delegate);
        this.visited = visited;
    }

    @Override
    public <T, S> void visitSource(Pipeline<T, S> pipeline) {
        this.cycle = !visited.add(pipeline);
        super.visitSource(pipeline);
    }

    @Override
    public ComponentVisitor visitComponent(ComponentType type, String name, String describe) {
        if (cycle) {
            return null;
        }

        ComponentVisitor componentVisitor = super.visitComponent(type, name, describe);
        return new ComponentCyclicVisitor(visited, componentVisitor);
    }
}
