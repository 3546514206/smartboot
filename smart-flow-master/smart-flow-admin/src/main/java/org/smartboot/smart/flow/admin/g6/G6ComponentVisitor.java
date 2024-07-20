package org.smartboot.smart.flow.admin.g6;

import org.smartboot.flow.core.Condition;
import org.smartboot.flow.core.ExtensionAttribute;
import org.smartboot.flow.core.attribute.AttributeHolder;
import org.smartboot.flow.core.attribute.Attributes;
import org.smartboot.flow.core.common.ComponentType;
import org.smartboot.flow.core.executable.Executable;
import org.smartboot.flow.core.parser.ExecutableTypeDetector;
import org.smartboot.flow.core.script.ScriptCondition;
import org.smartboot.flow.core.script.ScriptExecutable;
import org.smartboot.flow.core.script.ScriptExecutor;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.smartboot.flow.core.visitor.ComponentVisitor;
import org.smartboot.flow.core.visitor.ConditionVisitor;
import org.smartboot.flow.core.visitor.ExecutableVisitor;
import org.smartboot.flow.core.visitor.PipelineVisitor;
import org.smartboot.flow.helper.util.DecorateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author qinluo
 * @date 2023/2/10 20:28
 * @since 1.0.0
 */
public class G6ComponentVisitor extends ComponentVisitor {

    private final List<AttributeHolder> attributes = new ArrayList<>();
    private final String name;
    private String describe;
    private final ComponentType type;
    private G6PipelineVisitor pipeline;
    private String condition;
    private String branch;
    private final List<G6ComponentVisitor> components = new ArrayList<>();
    private String script;
    private String scriptType;
    private String scriptName;
    private String rollbackScriptName;
    private String rollbackScriptType;
    private String rollbackScript;
    private String executable;
    private Map<String, String> bindingAttrs = new HashMap<>(0);
    private List<ExtensionAttribute> extensionAttributes = new ArrayList<>(0);


    public G6ComponentVisitor(ComponentType type, String name, String describe) {
        this.name = name;
        this.describe = describe;
        this.type = type;
    }

    @Override
    public void visitExtensionAttributes(List<ExtensionAttribute> extensionAttributes) {
        this.extensionAttributes = extensionAttributes;
    }

    @Override
    public PipelineVisitor visitPipeline(String pipeline) {
        this.pipeline = new G6PipelineVisitor(pipeline);
        return this.pipeline;
    }

    @Override
    public ConditionVisitor visitCondition(String condition) {
        this.condition = condition;
        return new G6ConditionVisitor();
    }

    @Override
    public ComponentVisitor visitComponent(ComponentType type, String name, String describe) {
        G6ComponentVisitor component = new G6ComponentVisitor(type, name, describe);
        this.components.add(component);
        return component;
    }

    @Override
    public void visitAttributes(List<AttributeHolder> attributes) {
        this.attributes.addAll(attributes);
    }

    @Override
    public ExecutableVisitor visitExecutable(String executable) {
        this.executable = executable;
        return new G6ExecutableVisitor();
    }

    @Override
    public ComponentVisitor visitBranch(Object branch, ComponentType type, String name, String describe) {
        G6ComponentVisitor component = new G6ComponentVisitor(type, name, describe);
        component.branch = String.valueOf(branch);
        this.components.add(component);
        return component;
    }

    private void analyzeDescribe() {
        if (bindingAttrs.isEmpty()) {
            return;
        }

        // Decorate clazz
        Class<?> clazz = ExecutableTypeDetector.get().getJavaType(executable);
        if (clazz == null) {
            clazz = AuxiliaryUtils.asClass(executable);
        }

        // Cannot find any decorate clazz or custom clazz, for example reflect, shell.
        if (clazz == null) {
            return;
        }

        String decorated = DecorateUtils.decorateExecutable(clazz, bindingAttrs);
        describe = (decorated != null) ? decorated : describe;
    }

    public void analyze(G6Assembler assembler) {
        this.analyzeDescribe();
        double stored = assembler.getCx();
        AssertUtil.notBlank(name, "name is blank, " + describe);
        Node node = new Node();
        node.setLabel(!AuxiliaryUtils.isAnonymous(name) ? name : describe);
        node.setId(name);
        node.setComponentInfo(buildComponentInfo());

        if (type == ComponentType.IF || type == ComponentType.CHOOSE) {
            node.setLabel(condition);
            node.setY(assembler.incr());
            node.setX(assembler.getCx());

            assembler.set(node);
            String peek = assembler.comboStack().peek();
            if (peek != null) {
                node.setComboId(peek);
            }

            if (type == ComponentType.IF) {
                node.getAnchorPoints().add(Arrays.asList(0d, 0.5d));
                node.getAnchorPoints().add(Arrays.asList(1d, 0.5d));
                assembler.push(name, "true");
                // -> if -> then
                if (components.size() == 1) {
                    // 只有一个分支时默认右偏
                    assembler.incrVertical(1);
                    components.get(0).analyze(assembler);
                    // pop lastest element.
                    EdgeBranch lastest = assembler.pop();

                    // -> if -> then --|
                    //    |------------|---> next
                    List<String> all = lastest.getBranches();
                    all.add(name);
                    assembler.push(all);
                    // 还原
                    assembler.setCx(node.getX());

                    Edge trueEdge = assembler.findEdge(name, "true");
                    // Node的右边中间连接点
                    trueEdge.setSourceAnchor(4);
                    trueEdge.setTargetAnchor(0);
                    trueEdge.setType("polyline");

                } else {
                    double vertical = assembler.getVertical(assembler.getCy());
                    if (vertical != 0) {
                        assembler.setCx(vertical);
                    } else {
                        assembler.incrVertical(-1);
                    }

                    components.get(0).analyze(assembler);
                    // pop lastest element.
                    EdgeBranch thenLastest = assembler.pop();
                    // push if.
                    assembler.push(name, "false");

                    double thenY = assembler.getCy();
                    assembler.setCy(node.getY());
                    assembler.incr();

                    assembler.setCx(node.getX());
                    assembler.setCx(components.get(0).getX(assembler) + 2);

                    components.get(1).analyze(assembler);
                    // pop lastest element.
                    EdgeBranch elseLastest = assembler.pop();

                    List<String> all = thenLastest.getBranches();
                    all.addAll(elseLastest.getBranches());
                    // 存在环状则不需要再多加边
                    if (analyzeLoop(all, assembler)) {
                        assembler.push(all);
                    }

                    double elseY = assembler.getCy();
                    assembler.setCy(Math.max(thenY, elseY));
                    assembler.incr();

                    if (node.getX() != 0) {
                        balanceVertical(node, assembler);
                    }

                    Edge trueEdge = assembler.findEdge(name, "true");
                    // Node的右边中间连接点
                    trueEdge.setSourceAnchor(4);
                    trueEdge.setTargetAnchor(0);
                    trueEdge.setType("polyline");

                    Edge falseEdge = assembler.findEdge(name, "false");
                    // Node的左边中间连接点
                    falseEdge.setSourceAnchor(3);
                    falseEdge.setTargetAnchor(0);
                    falseEdge.setType("polyline");
                }
            } else {
                List<String> branch = new ArrayList<>();
                double max = assembler.getCy();
                int middle = components.size() / 2;
                double vertical = assembler.getVertical(assembler.getCy());

                for (int i = 0; i < components.size(); i++) {
                    G6ComponentVisitor p = components.get(i);
                    if (vertical == 0) {
                        assembler.setCx(node.getX() + (i - middle));
                    } else {
                        assembler.setCx(vertical + i*(1.5) + 0.2);
                    }


                    assembler.setCy(node.getY());
                    assembler.incr();
                    assembler.push(name, AuxiliaryUtils.or(p.branch, "default"));
                    p.analyze(assembler);
                    // pop lastest element.
                    EdgeBranch branchLastest = assembler.pop();
                    branch.addAll(branchLastest.getBranches());

                    max = Math.max(max, assembler.getCy() - 1);
                }
                // pop lastest element.
                if (analyzeLoop(branch, assembler)) {
                    assembler.push(branch);
                }

                assembler.setCy(max);
                assembler.incr();

                if (node.getX() != 0) {
                    balanceVertical(node, assembler);
                }
            }

        } else if (type == ComponentType.BASIC) {
            String peek = assembler.comboStack().peek();
            if (peek != null) {
                node.setComboId(peek);
            }
            node.setY(assembler.incr());
            node.setX(assembler.getCx());
            assembler.set(node);
            assembler.push(name);
        } else if (type == ComponentType.ADAPTER || type == ComponentType.SUBPROCESS) {
            // add combo
            Combo combo = new Combo();
            combo.setId(name);
            String peek = assembler.comboStack().peek();
            if (peek != null) {
                combo.setParentId(peek);
            }
            combo.setComponentInfo(buildComponentInfo());

            assembler.getCombos().add(combo);

            assembler.comboStack().push(name);
            boolean comboAdded = true;
            if (pipeline != null) {

                // 匿名子流程 combo不体现在图中
                if (AuxiliaryUtils.isAnonymous(pipeline.getName())) {
                    assembler.getCombos().remove(combo);
                    assembler.comboStack().pop();
                    comboAdded = false;
                } else {
                    combo.setLabel("子流程:" + pipeline.getName());
                }

                pipeline.analyze(assembler);
                if (this.attributes.stream().anyMatch(p -> p.getAttribute() == Attributes.REFERENCED_PIPELINE)
                        && pipeline.getNode(assembler) == null) {
                    // add edge.
                    G6PipelineVisitor g6PipelineVisitor = assembler.getNamedPipelineMap().get(pipeline.getName());
                    if (g6PipelineVisitor != null) {
                        Node entryNode = g6PipelineVisitor.getNode(assembler);
                        assembler.push(entryNode.getId());
                        assembler.getCombos().remove(combo);
                        assembler.comboStack().pop();
                        assembler.setCx(stored);
                        return;
                    }

                }
            } else {
                combo.setLabel("适配器: " + name);
                components.get(0).analyze(assembler);
            }

            if (comboAdded) {
                assembler.comboStack().pop();
            }

            assembler.setCy(assembler.getCy() + 0.3);
        }

        assembler.setCx(stored);
    }

    private boolean analyzeLoop(List<String> all, G6Assembler g6Assembler) {
        all.removeIf(p -> g6Assembler.findEdge(p, null) != null);
        return !all.isEmpty();
    }

    public double getX(G6Assembler assembler)  {
        if (this.type == ComponentType.BASIC || type == ComponentType.IF || type == ComponentType.CHOOSE) {
            return assembler.getNode(this.name).getX();
        } else if (type == ComponentType.SUBPROCESS) {
            return pipeline.getX(assembler);
        } else {
            return components.get(0).getX(assembler);
        }
    }

    public Node getNode(G6Assembler assembler) {
        if (this.type == ComponentType.BASIC || type == ComponentType.IF || type == ComponentType.CHOOSE) {
            return assembler.getNode(this.name);
        } else if (type == ComponentType.SUBPROCESS) {
            return pipeline.getNode(assembler);
        } else {
            return components.get(0).getNode(assembler);
        }
    }

    private void balanceVertical(Node node, G6Assembler assembler) {
        if (this.components.size() <= 1) {
            return;
        }

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        boolean found = false;
        for (G6ComponentVisitor component : this.components) {
            Node child = component.getNode(assembler);
            if (child == null) {
                continue;
            }

            min = Math.min(min, child.getX());
            max = Math.max(max, child.getX());
            found = true;
        }

        if (found) {
            // at middle.
            node.setX((min + max) / 2);
        }
    }

    private ComponentInfo buildComponentInfo() {
        ComponentInfo ci = new ComponentInfo();
        if (!AuxiliaryUtils.isAnonymous(name)) {
            ci.setName(name);
        }
        ci.setType(type.name());
        ci.setScript(script);
        ci.setScriptType(scriptType);
        ci.setScriptName(scriptName);
        ci.setRollbackScript(rollbackScript);
        ci.setRollbackScriptType(rollbackScriptType);
        ci.setRollbackScriptName(rollbackScriptName);
        ci.setExtensionAttributes(this.build(extensionAttributes));

        if (type == ComponentType.BASIC) {
            ci.setTypeDesc(script != null ? "基本组件（脚本）" : "基本组件");
            ci.setDescribe(executable);
        } else if (type == ComponentType.IF) {
            ci.setTypeDesc("IF组件");
            ci.setDescribe(condition);
        } else if (type == ComponentType.CHOOSE) {
            ci.setTypeDesc("CHOOSE组件");
            ci.setDescribe(condition);
        } else if (type == ComponentType.SUBPROCESS) {
            if (!AuxiliaryUtils.isAnonymous(this.pipeline.getName())) {
                ci.setName(this.pipeline.getName());
                ci.setDescribe(this.pipeline.getName());
            }
            ci.setTypeDesc("子流程组件");
        } else if (type == ComponentType.ADAPTER) {
            ci.setTypeDesc("适配组件");
            ci.setDescribe(executable);
        }

        List<AttrInfo> attributes = new ArrayList<>(32);
        ci.setAttributes(attributes);

        for (AttributeHolder ah : this.attributes) {
            if (ah.getValue() == null
                    || Objects.equals(ah.getAttribute(), Attributes.NAME)
                    || !ah.getAttribute().isVisible()) {
                continue;
            }

            AttrInfo ai = new AttrInfo();
            ai.setName(ah.getAttribute().getName());
            ai.setValue(processValue(ah.getValue()));
            ai.setRemark(ah.getAttribute().getDescription());
            attributes.add(ai);
        }

        return ci;
    }

    private String processValue(Object value) {
        if (value instanceof List) {
            StringBuilder sb = new StringBuilder();
            ((List<Object>)value).forEach(p -> sb.append(p.toString()).append(","));
            String str = sb.toString();
            return str.substring(0, str.length() - 1);
        } else {
            return value.toString();
        }
    }

    private String build(List<ExtensionAttribute> eas) {
        Set<String> appended = new HashSet<>(32);
        StringBuilder sb = new StringBuilder();

        eas.forEach(p -> p.getValues().forEach((k, v) -> {
            if (appended.add(k)) {
                sb.append(k).append("=").append(v).append("\r\n");
            }
        }));

        return sb.toString();
    }

    private class G6ConditionVisitor extends ConditionVisitor {
        @Override
        public <T, S> void visitSource(Condition<T, S> condition) {
            if (condition instanceof ScriptCondition) {
                ScriptCondition<T, S> nc = (ScriptCondition<T, S>) condition;
                ScriptExecutor<T, S> scriptExecutor = nc.getScriptExecutor();
                G6ComponentVisitor.this.scriptName = scriptExecutor.getName();
                G6ComponentVisitor.this.scriptType = scriptExecutor.getType();
                G6ComponentVisitor.this.script = scriptExecutor.getScript();
                G6ComponentVisitor.this.condition = scriptExecutor.getName();
            }
        }
    }

    private class G6ExecutableVisitor extends ExecutableVisitor {

        @Override
        public void visitBindingAttrs(Map<String, String> attrs) {
            G6ComponentVisitor.this.bindingAttrs = attrs;
        }

        @Override
        public <T, S> void visitSource(Executable<T, S> executable) {
            if (executable instanceof ScriptExecutable) {
                ScriptExecutable<T, S> sc = (ScriptExecutable<T, S>) executable;
                ScriptExecutor<T, S> scriptExecutor = sc.getScriptExecutor();
                G6ComponentVisitor.this.scriptName = scriptExecutor.getName();
                G6ComponentVisitor.this.scriptType = scriptExecutor.getType();
                G6ComponentVisitor.this.script = scriptExecutor.getScript();

                // Ensure executable name is scriptExecutor's name.
                G6ComponentVisitor.this.executable = scriptExecutor.getName();

                ScriptExecutor<T, S> rollbackExecutor = sc.getRollbackExecutor();
                if (rollbackExecutor != null) {
                    G6ComponentVisitor.this.rollbackScriptName = rollbackExecutor.getName();
                    G6ComponentVisitor.this.rollbackScriptType = rollbackExecutor.getType();
                    G6ComponentVisitor.this.rollbackScript = rollbackExecutor.getScript();
                }
            } else {
                String phrase = ExecutableTypeDetector.get().getPhrase(executable.getClass());
                if (AuxiliaryUtils.isNotBlank(phrase)) {
                    G6ComponentVisitor.this.executable = phrase;
                }
            }
        }
    }
}
