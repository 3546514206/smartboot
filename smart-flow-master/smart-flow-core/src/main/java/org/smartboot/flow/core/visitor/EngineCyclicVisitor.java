package org.smartboot.flow.core.visitor;

import org.smartboot.flow.core.FlowEngine;

import java.util.HashSet;
import java.util.Set;

/**
 * @author huqiang
 * @since 2023/3/1 20:08
 */
public class EngineCyclicVisitor extends EngineVisitor {

    /**
     * 记录访问过的对象
     */
    private final Set<Object> visited = new HashSet<>();

    public EngineCyclicVisitor(EngineVisitor delegate) {
        super(delegate);
    }

    @Override
    public <T, S> void visitSource(FlowEngine<T, S> flowEngine) {
        this.visited.add(flowEngine);
        super.visitSource(flowEngine);
    }

    @Override
    public PipelineVisitor visitPipeline(String pipeline) {
        PipelineVisitor pipelineVisitor = super.visitPipeline(pipeline);
        return new PipelineCyclicVisitor(visited, pipelineVisitor);
    }
}
