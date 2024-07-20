package org.smartboot.flow.helper.view;

import org.smartboot.flow.core.Condition;
import org.smartboot.flow.core.attribute.AttributeHolder;
import org.smartboot.flow.core.attribute.Attributes;
import org.smartboot.flow.core.common.ComponentType;
import org.smartboot.flow.core.component.Component;
import org.smartboot.flow.core.parser.ExecutableTypeDetector;
import org.smartboot.flow.core.script.ScriptCondition;
import org.smartboot.flow.core.script.ScriptExecutor;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.smartboot.flow.core.visitor.ComponentVisitor;
import org.smartboot.flow.core.visitor.ConditionVisitor;
import org.smartboot.flow.core.visitor.ExecutableVisitor;
import org.smartboot.flow.core.visitor.PipelineVisitor;
import org.smartboot.flow.helper.util.DecorateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yamikaze
 * @date 2022/11/14
 */
public class PlantumlComponent extends ComponentVisitor {

    private final List<AttributeHolder> attributes = new ArrayList<>();
    private final String name;
    private String describe;
    private final ComponentType type;
    private PlantumlPipeline pipeline;
    private String condition;
    private String branch;
    private final List<PlantumlComponent> components = new ArrayList<>();
    private volatile boolean eraserCalled;
    private Component<?, ?> component;
    private Map<String, String> bindingAttrs = new HashMap<>(0);


    public PlantumlComponent(ComponentType type, String name, String describe) {
        this.name = name;
        this.describe = describe;
        this.type = type;
    }

    @Override
    public <T, S> void visitSource(Component<T, S> component) {
        super.visitSource(component);
        this.component = component;
    }

    @Override
    public ExecutableVisitor visitExecutable(String executable) {
        return new PlantumlExecutableVisitor();
    }

    @Override
    public PipelineVisitor visitPipeline(String pipeline) {
        this.pipeline = new PlantumlPipeline(pipeline);
        return this.pipeline;
    }

    @Override
    public ConditionVisitor visitCondition(String condition) {
        this.condition = condition;
        return new PlantumlConditionVisitor();
    }

    @Override
    public ComponentVisitor visitComponent(ComponentType type, String name, String describe) {
        PlantumlComponent component = new PlantumlComponent(type, name, describe);
        this.components.add(component);
        return component;
    }

    @Override
    public void visitAttributes(List<AttributeHolder> attributes) {
        this.attributes.addAll(attributes);
    }

    @Override
    public ComponentVisitor visitBranch(Object branch, ComponentType type, String name, String describe) {
        PlantumlComponent component = new PlantumlComponent(type, name, describe);
        component.branch = (String.valueOf(branch));
        this.components.add(component);
        return component;
    }

    public void generate(StringBuilder content) {
        this.analyzeDescribe();
        String nodeName = AuxiliaryUtils.isAnonymous(name) ? describe : name;
        ComponentType type = this.type;

        if (type == ComponentType.BASIC) {
            String serialAttributes = serialAttributes();
            if (isAsync()) {
                content.append("-[dashed]->异步;\n");
            }

            content.append(getColor()).append(":").append(nodeName).append(";\n");
            if (serialAttributes.trim().length() > 0) {
                content.append("note right\n").append(serialAttributes)
                        .append("\nend note\n");
            }

        } else if (type == ComponentType.IF) {
            if (isAsync()) {
                content.append("-[dashed]->异步;\n");
            }
            content.append("if (").append(condition).append(") then (true)\n");
            components.get(0).generate(content);
            if (components.size() > 1) {
                content.append("else (false)\n");
                components.get(1).generate(content);
            }
            content.append("endif\n");

        } else if (type == ComponentType.CHOOSE) {
            if (isAsync()) {
                content.append("-[dashed]->异步;\n");
            }
            content.append("switch (").append(condition).append(")\n");

            for (PlantumlComponent component : components) {
                if (component.branch != null) {
                    content.append("case (branch ").append(component.branch).append(")\n");
                } else {
                    content.append("case (<color:red>default) \n");
                }
                component.generate(content);
            }

            content.append("endswitch\n");

        } else if (type == ComponentType.SUBPROCESS) {
            if (isAsync()) {
                content.append("-[dashed]->异步;\n");
            }
            if (pipeline.getComponents().isEmpty()) {
                content.append(": goto ").append(pipeline.getName()).append(";\n");
                // kill this branch.
                content.append("kill\n ");
                return;
            }

            if (!AuxiliaryUtils.isAnonymous(pipeline.getName())) {
                content.append("partition 子流程：").append(pipeline.getName()).append("{ \n");
            }

            pipeline.generate(content);

            if (!AuxiliaryUtils.isAnonymous(pipeline.getName())) {
                content.append(" } \n");
            }

        } else if (type == ComponentType.ADAPTER) {
            if (isAsync()) {
                content.append("-[dashed]->异步;\n");
            }
            PlantumlComponent pipeline = components.get(0);
            content.append("partition 适配器：").append(nodeName)
                    .append(" 子流程：")
                    .append(pipeline.pipeline.getName()).append("{ \n");
            pipeline.pipeline.generate(content);
            content.append(" } \n");
        }
    }

    private String serialAttributes() {
        StringBuilder sb = new StringBuilder();
        attributes.stream().filter(p ->
                        p.getAttribute() != Attributes.ROLLBACK && p.getAttribute() != Attributes.DEGRADABLE
                        && p.getAttribute() != Attributes.ASYNC && p.getAttribute() != Attributes.ENABLED
                                && p.getAttribute() != Attributes.NAME)
                .forEach(p -> sb.append(p.getAttribute().name()).append(":").append(p.getValue()).append(";"));
        return sb.toString();
    }

    private boolean isAsync() {
        return component.isAsync();
    }

    private String getColor() {
        List<Color> colors = new ArrayList<>();
        AttributeHolder holder = attributes.stream().filter(p -> p.getAttribute() == Attributes.ROLLBACK).findFirst().orElse(null);
        if (holder != null && Boolean.parseBoolean(String.valueOf(holder.getValue()))) {
            colors.add(Color.ROLLBACKABLE);
        }

        if (component.isAsync()) {
            colors.add(Color.ASYNC);
        }

        if (component.isDegradable()) {
            colors.add(Color.DEGRADABLE);
        }

        if (!component.isEnabled()) {
            colors.clear();
            colors.add(Color.DISABLED);
        }

        if (colors.size() == 0) {
            return "";
        }

        String base = colors.get(0).getColor();
        for (int i = 1; i < colors.size(); i++) {
            base = colors.get(i).mix(base);
        }

        return base;
    }

    public void eraser(boolean directPipeline) {
        if (eraserCalled) {
            return;
        }

        eraserCalled = true;

        // 未直接在流水线中，需要擦除所有属性，除了name， 如果是基本组件，可以保留degradable和callback
        if (!directPipeline) {
            this.attributes.removeIf(p -> {
                if (type != ComponentType.BASIC) {
                    return p.getAttribute() != Attributes.NAME;
                }

                return p.getAttribute() != Attributes.DEGRADABLE && p.getAttribute() != Attributes.DEGRADE_CALLBACK;
            });
        }

        if (this.type == ComponentType.SUBPROCESS) {
            this.pipeline.eraser();
        } else if (this.type != ComponentType.BASIC) {
            this.components.forEach(p -> p.eraser(false));
        }
    }

    private void analyzeDescribe() {
        if (bindingAttrs.isEmpty()) {
            return;
        }

        // Decorate clazz
        Class<?> clazz = ExecutableTypeDetector.get().getJavaType(describe);
        if (clazz == null) {
            clazz = AuxiliaryUtils.asClass(describe);
        }

        // Cannot find any decorate clazz or custom clazz, for example reflect, shell.
        if (clazz == null) {
            return;
        }

        String decorated = DecorateUtils.decorateExecutable(clazz, bindingAttrs);
        describe = (decorated != null) ? decorated : describe;
    }

    private class PlantumlConditionVisitor extends ConditionVisitor {

        @Override
        public <T, S> void visitSource(Condition<T, S> condition) {
            if (condition instanceof ScriptCondition) {
                ScriptCondition<T, S> sc = (ScriptCondition<T, S>) condition;
                ScriptExecutor<T, S> scriptExecutor = sc.getScriptExecutor();
                PlantumlComponent.this.condition = scriptExecutor.getName() + "-" + scriptExecutor.getType();
            }
        }
    }

    private class PlantumlExecutableVisitor extends ExecutableVisitor {

        @Override
        public void visitBindingAttrs(Map<String, String> attrs) {
            PlantumlComponent.this.bindingAttrs = attrs;
        }
    }

}
