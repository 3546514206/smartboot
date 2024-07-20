package org.smartboot.flow.helper.view;

import org.smartboot.flow.core.common.ComponentType;
import org.smartboot.flow.core.visitor.ComponentVisitor;
import org.smartboot.flow.core.visitor.PipelineVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yamikaze
 * @date 2022/11/14
 */
public class PlantumlPipeline extends PipelineVisitor {

    private volatile boolean eraserCalled;
    private final String name;
    private final List<PlantumlComponent> components = new ArrayList<>();

    public String getName() {
        return name;
    }


    public PlantumlPipeline(String name) {
        this.name = name;
    }

    public List<PlantumlComponent> getComponents() {
        return components;
    }

    @Override
    public ComponentVisitor visitComponent(ComponentType type, String name, String describe) {
        PlantumlComponent component = new PlantumlComponent(type, name, describe);
        this.components.add(component);
        return component;
    }

    public void generate(StringBuilder content) {
        for (PlantumlComponent component : components) {
            component.generate(content);
        }

    }

    public void eraser() {
        if (eraserCalled) {
            return;
        }

        eraserCalled = true;

        for (PlantumlComponent component : components) {
            component.eraser(true);
        }
    }
}
