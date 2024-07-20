package org.smartboot.smart.flow.admin.g6;

import org.smartboot.flow.core.common.ComponentType;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.smartboot.flow.core.visitor.ComponentVisitor;
import org.smartboot.flow.core.visitor.PipelineVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qinluo
 * @date 2023/2/10 20:28
 * @since 1.0.0
 */
public class G6PipelineVisitor extends PipelineVisitor {

    private final List<G6ComponentVisitor> componentVisitors = new ArrayList<>();
    private final String name;

    public G6PipelineVisitor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public ComponentVisitor visitComponent(ComponentType type, String name, String describe) {
        G6ComponentVisitor component = new G6ComponentVisitor(type, name, describe);
        this.componentVisitors.add(component);
        return component;
    }

    public void analyze(G6Assembler assembler) {
        if (!AuxiliaryUtils.isAnonymous(this.name) && componentVisitors.size() > 0) {
            assembler.getNamedPipelineMap().put(name, this);
        }

        for (G6ComponentVisitor component : componentVisitors) {
            component.analyze(assembler);
        }
    }

    public double getX(G6Assembler assembler) {
        if (this.componentVisitors.isEmpty()) {
            return 0;
        }

        return componentVisitors.get(0).getX(assembler);
    }

    public Node getNode(G6Assembler assembler) {
        if (this.componentVisitors.isEmpty()) {
            return null;
        }

        return componentVisitors.get(0).getNode(assembler);
    }
}
