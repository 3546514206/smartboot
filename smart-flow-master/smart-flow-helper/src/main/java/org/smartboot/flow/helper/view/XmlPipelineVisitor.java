package org.smartboot.flow.helper.view;

import org.smartboot.flow.core.common.ComponentType;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.smartboot.flow.core.visitor.ComponentVisitor;
import org.smartboot.flow.core.visitor.PipelineVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yamikaze
 * @date 2022/11/14
 * @since 1.0.5
 */
public class XmlPipelineVisitor extends PipelineVisitor {

    private final String name;
    private final List<XmlComponentVisitor> components = new ArrayList<>();

    public String getName() {
        return name;
    }

    public XmlPipelineVisitor(String name) {
        this.name = name;
    }

    @Override
    public ComponentVisitor visitComponent(ComponentType type, String name, String describe) {
        XmlComponentVisitor component = new XmlComponentVisitor(type, name, describe);
        this.components.add(component);
        return component;
    }

    public void generate(StringBuilder content, int numbersOfTab) {
        // for anonymous-pipeline
        boolean incrementTab = false;
        //<pipeline name="subprocess1">
        if (name != null && !AuxiliaryUtils.isAnonymous(name)) {
            incrementTab = true;
            AuxiliaryUtils.appendTab(content, numbersOfTab);
            content.append("<pipeline name=\"").append(name).append("\">\n");
            // Already processed.
            PipelineCollector.processed(name);
        }

        for (XmlComponentVisitor component : components) {
            component.generate(content, incrementTab ? numbersOfTab + 1 : numbersOfTab);
        }

        if (name != null && !AuxiliaryUtils.isAnonymous(name)) {
            AuxiliaryUtils.appendTab(content, numbersOfTab);
            content.append("</pipeline>\n");
        }

    }
}
