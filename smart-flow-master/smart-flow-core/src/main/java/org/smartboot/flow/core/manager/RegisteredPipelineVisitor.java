package org.smartboot.flow.core.manager;

import org.smartboot.flow.core.Pipeline;
import org.smartboot.flow.core.common.ComponentType;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.smartboot.flow.core.visitor.ComponentVisitor;
import org.smartboot.flow.core.visitor.PipelineVisitor;

import java.util.Set;

/**
 * @author qinluo
 * @date 2022/11/19 12:26
 * @since 1.0.0
 */
public class RegisteredPipelineVisitor extends PipelineVisitor {

    private final PipelineModel pipelineModel;
    private final Set<Object> visited;
    private boolean isCycle;
    private final IdentifierAllocator allocator;

    public RegisteredPipelineVisitor(PipelineModel pipelineModel, Set<Object> visited, IdentifierAllocator allocator) {
        this.pipelineModel = pipelineModel;
        this.visited = visited;
        this.allocator = allocator;
    }

    @Override
    public <T, S> void visitSource(Pipeline<T, S> pipeline) {
        this.isCycle = !this.visited.add(pipeline);
    }

    @Override
    public ComponentVisitor visitComponent(ComponentType type, String name, String describe) {
        // Avoid stackOverflow
        if (isCycle) {
            return null;
        }

        // Re-allocate name.
        if (name == null || AuxiliaryUtils.isAnonymous(name)) {
            name = allocator.allocate(type.name());
        }

        ComponentModel comp = new ComponentModel(type, name);
        this.pipelineModel.addComponent(comp);
        return new RegisteredComponentVisitor(comp, visited, allocator);
    }
}
