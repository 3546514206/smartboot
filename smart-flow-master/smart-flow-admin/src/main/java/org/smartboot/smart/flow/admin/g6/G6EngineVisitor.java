package org.smartboot.smart.flow.admin.g6;

import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.core.visitor.EngineCyclicVisitor;
import org.smartboot.flow.core.visitor.EngineVisitor;
import org.smartboot.flow.core.visitor.PipelineVisitor;

import java.util.concurrent.Executor;

/**
 * @author qinluo
 * @date 2023/2/10 20:27
 * @since 1.0.0
 */
public class G6EngineVisitor extends EngineVisitor {

    private String engineName;
    private G6PipelineVisitor pipelineVisitor;
    private final G6Result g6Result = new G6Result();
    private static final Node START = new Node();
    private static final int INIT_AXIS = 0;

    static {
        START.setX(INIT_AXIS);
        START.setY(INIT_AXIS);
        START.setId("#start");
        START.setLabel("start");
    }

    public <T, S> void visit(FlowEngine<T, S> engine) {
        engine.accept(new EngineCyclicVisitor(this));
    }

    public G6Result getResult() {
        return g6Result;
    }

    @Override
    public void visitEnd() {
        // 分析计算
        g6Result.setName(engineName);
        g6Result.setProcess(pipelineVisitor.getName());

        G6Assembler assembler = new G6Assembler();
        assembler.setCx(INIT_AXIS);
        assembler.setCy(INIT_AXIS + 1);

        // Start Node and edge.
        assembler.set(START);
        assembler.push(START.getId());

        // Start analyze.
        pipelineVisitor.analyze(assembler);

        Node end = new Node();
        end.setX(INIT_AXIS);
        end.setY(assembler.getCy());
        end.setId("#end");
        end.setLabel("end");

        // End Node and edge
        assembler.set(end);
        assembler.push(end.getId());

        g6Result.setEdges(assembler.getEdges());
        g6Result.getNodes().addAll(assembler.getNodes());
        g6Result.getCombos().addAll(assembler.getCombos());
    }

    @Override
    public void visit(String name, Executor executor) {
        this.engineName = name;
    }

    @Override
    public <T, S> void visitSource(FlowEngine<T, S> flowEngine) {
        super.visitSource(flowEngine);
    }

    @Override
    public PipelineVisitor visitPipeline(String pipeline) {
        pipelineVisitor = new G6PipelineVisitor(pipeline);
        return pipelineVisitor;
    }
}
