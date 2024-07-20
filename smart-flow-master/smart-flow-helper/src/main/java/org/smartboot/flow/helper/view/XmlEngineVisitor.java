package org.smartboot.flow.helper.view;

import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.core.common.Pair;
import org.smartboot.flow.core.visitor.EngineCyclicVisitor;
import org.smartboot.flow.core.visitor.EngineVisitor;
import org.smartboot.flow.core.visitor.PipelineVisitor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * @author qinluo
 * @date 2022-11-14 19:48:14
 * @since 1.0.5
 */
public class XmlEngineVisitor extends EngineVisitor {

    private static final String START = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<engines xmlns=\"http://org.smartboot/smart-flow\"\n" +
            "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "       xsi:schemaLocation=\"http://org.smartboot/smart-flow\n" +
            "                           http://org.smartboot/smart-flow-1.0.1.xsd\">";

    private static final String END = "</engines>";

    /**
     * engine name.
     */
    private String name;
    private XmlPipelineVisitor pipeline;

    /**
     * Xml content.
     */
    private String content;

    /**
     * 是否替换反序列化文本中的换行符、tab等多余空白
     *
     * @since 1.1.4
     */
    private boolean compress;

    public void compressContent() {
        this.compress = true;
    }

    public String getContent() {
        return content;
    }

    public <T, S> void visit(FlowEngine<T, S> engine) {
        engine.accept(new EngineCyclicVisitor(this));
    }

    @Override
    public void visitEnd() {
        ScriptCollector.start();
        PipelineCollector.start();

        StringBuilder content = new StringBuilder();
        content.append(START).append("\n");
        // <engine name="" pipeline=""/>
        content.append("\t<engine name=\"").append(name).append("\" pipeline=\"").append(pipeline.getName()).append("\"/>\n");

        // Pipeline
        this.pipeline.generate(content, 1);

        // process ref pipelines.
        List<XmlPipelineVisitor> unprocessed = PipelineCollector.getUnprocessed();
        while (!unprocessed.isEmpty()) {
            unprocessed.forEach(p -> p.generate(content, 1));
            unprocessed = PipelineCollector.getUnprocessed();
        }

        // Avoid process script.
        if (compress) {
            this.content = this.content.replace("\n", "")
                    .replace("\t", "").replace("\r\n", "");
        }

        Map<String, Pair<String, String>> scripts = ScriptCollector.end();
        if (scripts != null && scripts.size() > 0) {
            scripts.forEach((k, v) -> {
                    if (!compress) {
                        content.append("\n\t");
                    }
                    content.append("<script name=\"").append(k)
                    .append("\" type=\"").append(v.getRight())
                    .append("\">").append("<![CDATA[").append(v.getLeft()).append("]]>").append("</script>");
            });
        }

        if (!compress) {
            content.append("\n").append(END).append("\n");
        } else {
            content.append(END);
        }
        this.content = content.toString();
    }

    @Override
    public void visit(String engine, Executor executor) {
        this.name = engine;
    }

    @Override
    public PipelineVisitor visitPipeline(String pipeline) {
        this.pipeline = new XmlPipelineVisitor(pipeline);
        return this.pipeline;
    }
}
