package org.smartboot.flow.helper.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.visitor.EngineCyclicVisitor;
import org.smartboot.flow.core.visitor.EngineVisitor;
import org.smartboot.flow.core.visitor.PipelineVisitor;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.Executor;

/**
 * @author qinluo
 * @date 2022-11-14 19:48:14
 * @since 1.0.0
 */
public class PlantumlEngineVisitor extends EngineVisitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlantumlEngineVisitor.class);

    private static final String START = "@startuml";
    private static final String END = "@enduml";
    private static final String SUFFIX = ".puml";

    /** plantuml file dest. */
    private final File dest;
    /**
     * engine name.
     */
    private String name;
    private PlantumlPipeline pipeline;

    public PlantumlEngineVisitor(String dest) {
        this(new File(dest + SUFFIX));
    }

    public PlantumlEngineVisitor(File dest) {
        AssertUtil.notNull(dest, "dest must not be null!");
        this.dest = dest;
    }

    public <T, S> void visit(FlowEngine<T, S> engine) {
        engine.accept(new EngineCyclicVisitor(this));
    }

    @Override
    public void visitEnd() {
        // eraser不需要的属性
        this.pipeline.eraser();

        StringBuilder content = new StringBuilder();
        content.append(START).append("\n");
        content.append("skinparam ConditionEndStyle hline\n");
        content.append("title Engine:").append(this.name)
                .append(",MainProcess:").append(pipeline.getName()).append("\n");

        content.append("split\n");
        for (Color color : Color.values()) {
            /*
             * split again
             * -[hidden]->
             * #yellow :异步组件;
             * -[hidden]->
             * kill
             */
            if (color.ordinal() > 0) {
                content.append("split again\n");
            }
            content.append("-[hidden]->\n")
                    .append(color.getColor()).append(":").append(color.getDesc()).append(";\n")
                    .append("-[hidden]->\n").append("kill\n");

        }
        content.append("\nend split\n");

        content.append("\n : start ;\n");

        this.pipeline.generate(content);

        content.append("\n : end ;\n");

        content.append("\n").append(END).append("\n");
        try {
            FileOutputStream fos = new FileOutputStream(dest);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(fos)));
            writer.write(content.toString());
            writer.flush();
        } catch (Exception e) {
            LOGGER.error("write to dest {} error", dest, e);
        }

    }

    @Override
    public void visit(String engine, Executor executor) {
        this.name = engine;
    }

    @Override
    public PipelineVisitor visitPipeline(String pipeline) {
        this.pipeline = new PlantumlPipeline(pipeline);
        return this.pipeline;
    }
}
