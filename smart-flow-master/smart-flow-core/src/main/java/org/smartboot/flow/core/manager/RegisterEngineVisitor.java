package org.smartboot.flow.core.manager;

import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.core.visitor.EngineVisitor;
import org.smartboot.flow.core.visitor.PipelineVisitor;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * @author qinluo
 * @date 2022/11/18 23:02
 * @since 1.0.0
 */
public class RegisterEngineVisitor extends EngineVisitor {

    private EngineModel model;
    private final Set<Object> visited = new HashSet<>(32);
    private final IdentifierAllocator allocator = new IdentifierAllocator();

    @Override
    public void visit(String name, Executor executor) {
        this.model = new EngineModel(name);
    }

    @Override
    public <T, S> void visitSource(FlowEngine<T, S> flowEngine) {
        visited.add(flowEngine);
        this.model.setSource(flowEngine);
    }

    @Override
    public PipelineVisitor visitPipeline(String pipeline) {
        PipelineModel pipelineModel = new PipelineModel(pipeline, pipeline);
        this.model.setPipeline(pipelineModel);
        return new RegisteredPipelineVisitor(pipelineModel, visited, allocator);
    }

    @Override
    public void visitEnd() {
        this.model.init(visited);
        this.model.collect();
    }

    public EngineModel getEngine() {
        return model;
    }
}
