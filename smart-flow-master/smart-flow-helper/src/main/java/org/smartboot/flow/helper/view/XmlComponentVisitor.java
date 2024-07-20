package org.smartboot.flow.helper.view;

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
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.smartboot.flow.core.visitor.ComponentVisitor;
import org.smartboot.flow.core.visitor.ConditionVisitor;
import org.smartboot.flow.core.visitor.ExecutableVisitor;
import org.smartboot.flow.core.visitor.PipelineVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yamikaze
 * @date 2022/11/14
 * @since 1.0.5
 */
public class XmlComponentVisitor extends ComponentVisitor {

    private final List<AttributeHolder> attributes = new ArrayList<>(0);
    private final ComponentType type;
    private XmlPipelineVisitor pipeline;
    private String condition;
    private String branch;
    private final List<XmlComponentVisitor> components = new ArrayList<>(0);
    private String executable;
    private final String name;
    private String script;
    private String scriptName;
    private String scriptType;
    private String rollbackScript;
    private String rollbackScriptName;
    private String rollbackScriptType;
    private boolean referencedPipeline;

    private Map<String, String> bindingAttrs = new HashMap<>(0);
    private List<ExtensionAttribute> extensionAttributes = new ArrayList<>(0);

    public XmlComponentVisitor(ComponentType type, String name, String ignoredDescribe) {
        this.type = type;
        this.name = name;
    }

    @Override
    public ExecutableVisitor visitExecutable(String executable) {
        this.executable = executable;
        return new XmlExecutableVisitor();
    }

    @Override
    public void visitExtensionAttributes(List<ExtensionAttribute> extensionAttributes) {
        this.extensionAttributes = extensionAttributes;
    }

    @Override
    public PipelineVisitor visitPipeline(String pipeline) {
        this.pipeline = new XmlPipelineVisitor(pipeline);
        return this.pipeline;
    }

    @Override
    public ConditionVisitor visitCondition(String condition) {
        this.condition = condition;
        return new XmlConditionVisitor();
    }

    @Override
    public ComponentVisitor visitComponent(ComponentType type, String name, String describe) {
        XmlComponentVisitor component = new XmlComponentVisitor(type, name, describe);
        this.components.add(component);
        return component;
    }

    @Override
    public void visitAttributes(List<AttributeHolder> attributes) {
        this.attributes.addAll(attributes);
        this.referencedPipeline = attributes.stream().anyMatch(p -> p.getAttribute() == Attributes.REFERENCED_PIPELINE);
    }

    @Override
    public ComponentVisitor visitBranch(Object branch, ComponentType type, String name, String describe) {
        XmlComponentVisitor component = new XmlComponentVisitor(type, name, describe);
        component.branch = (String.valueOf(branch));
        this.components.add(component);
        return component;
    }

    private void appendAttributes(StringBuilder content, int numbersOfTab) {
        for (AttributeHolder ah : attributes) {
            if (ah.getAttribute().isVisible()) {
                content.append(ah.getAttribute().getName()).append("=\"").append(processValue(ah.getValue())).append("\" ");
            }
        }

        Set<String> appended = new HashSet<>(32);
        for (ExtensionAttribute ea : extensionAttributes) {
            content.append("\n");
            AuxiliaryUtils.appendTab(content, numbersOfTab + 1);
            ea.getValues().forEach((k,v) -> {
                if (appended.add(k)) {
                    content.append(k).append("=\"").append(v).append("\" ");
                }
            });
        }

        this.bindingAttrs.forEach((k ,v) -> {
            content.append("\n");
            AuxiliaryUtils.appendTab(content, numbersOfTab + 1);
            if (appended.add(k)) {
                content.append(k).append("=\"").append(v).append("\" ");
            }
        });
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

    public void generate(StringBuilder content, int numbersOfTab) {
        if (name != null && !AuxiliaryUtils.isAnonymous(name)) {
            addAttributeName();
        }

        if (script != null) {
            ScriptCollector.collect(scriptName, script, scriptType);
        }

        if (rollbackScript != null) {
            ScriptCollector.collect(rollbackScriptName, rollbackScript, rollbackScriptType);
        }

        if (type == ComponentType.BASIC) {
            AuxiliaryUtils.appendTab(content, numbersOfTab);
            content.append("<component execute=\"").append(executable).append("\" ");
            appendAttributes(content, numbersOfTab);
            content.append("/>\n");
        } else if (type == ComponentType.IF) {
            AuxiliaryUtils.appendTab(content, numbersOfTab);
            content.append("<if test=\"").append(condition).append("\" ");
            appendAttributes(content, numbersOfTab);
            content.append(">\n");

            AuxiliaryUtils.appendTab(content, numbersOfTab + 1);
            content.append("<then>\n");
            components.get(0).generate(content, numbersOfTab + 2);
            AuxiliaryUtils.appendTab(content, numbersOfTab + 1);
            content.append("</then>\n");

            if (components.size() > 1) {
                AuxiliaryUtils.appendTab(content, numbersOfTab + 1);
                content.append("<else>\n");
                components.get(1).generate(content, numbersOfTab + 2);
                AuxiliaryUtils.appendTab(content, numbersOfTab + 1);
                content.append("</else>\n");
            }
            AuxiliaryUtils.appendTab(content, numbersOfTab);
            content.append("</if>\n");
        } else if (type == ComponentType.CHOOSE) {
            AuxiliaryUtils.appendTab(content, numbersOfTab);
            content.append("<choose test=\"").append(condition).append("\" ");
            appendAttributes(content, numbersOfTab);
            content.append(">\n");

            for (XmlComponentVisitor component : components) {
                if (component.branch != null) {
                    AuxiliaryUtils.appendTab(content, numbersOfTab + 1);
                    content.append("<case when=\"").append(component.branch).append("\" >\n");
                    component.generate(content, numbersOfTab + 2);
                    AuxiliaryUtils.appendTab(content, numbersOfTab + 1);
                    content.append("</case>\n");
                } else {
                    AuxiliaryUtils.appendTab(content, numbersOfTab + 1);
                    content.append("<default>\n");
                    component.generate(content, numbersOfTab + 2);
                    AuxiliaryUtils.appendTab(content, numbersOfTab + 1);
                    content.append("</default>\n");
                }
            }
            AuxiliaryUtils.appendTab(content, numbersOfTab);
            content.append("</choose>\n");

        } else if (type == ComponentType.SUBPROCESS) {
            // Referenced pipeline && not Anonymous process
            if (this.referencedPipeline
                    && !AuxiliaryUtils.isAnonymous(pipeline.getName())) {
                AuxiliaryUtils.appendTab(content, numbersOfTab);
                content.append("<component subprocess=\"")
                        .append(pipeline.getName()).append("\" ");
                appendAttributes(content, numbersOfTab);
                content.append("/>\n");
                PipelineCollector.collect(pipeline.getName(), pipeline);
                return;
            }

            // Nested pipeline tag.
            pipeline.generate(content, numbersOfTab);
        } else if (type == ComponentType.ADAPTER) {
            AuxiliaryUtils.appendTab(content, numbersOfTab);
            content.append("<adapter execute=\"").append(executable).append("\" ");
            appendAttributes(content, numbersOfTab);
            content.append(">\n");
            XmlComponentVisitor pipeline = components.get(0);
            pipeline.pipeline.generate(content, numbersOfTab + 1);
            AuxiliaryUtils.appendTab(content, numbersOfTab);
            content.append("</adapter>\n");
        }
    }

    private void addAttributeName() {
        // 通过其他手段设置的name，反设置到属性列表
        AttributeHolder ab = attributes.stream().filter(p -> p.getAttribute() == Attributes.NAME).findFirst().orElse(null);
        if (ab == null) {
            attributes.add(AttributeHolder.of(Attributes.NAME, name));
        }

    }

    private class XmlConditionVisitor extends ConditionVisitor {

        @Override
        public <T, S> void visitSource(Condition<T, S> condition) {
            if (condition instanceof ScriptCondition) {
                ScriptCondition<T, S> sc = (ScriptCondition<T, S>) condition;
                ScriptExecutor<T, S> scriptExecutor = sc.getScriptExecutor();
                XmlComponentVisitor.this.scriptName = scriptExecutor.getName();
                XmlComponentVisitor.this.condition = scriptExecutor.getName();
                XmlComponentVisitor.this.scriptType = scriptExecutor.getType();
                XmlComponentVisitor.this.script = scriptExecutor.getScript();
            }
        }
    }

    private class XmlExecutableVisitor extends ExecutableVisitor {

        @Override
        public void visitBindingAttrs(Map<String, String> attrs) {
            XmlComponentVisitor.this.bindingAttrs = attrs;
        }

        @Override
        public <T, S> void visitSource(Executable<T, S> executable) {
            if (executable instanceof ScriptExecutable) {
                ScriptExecutable<T, S> sc = (ScriptExecutable<T, S>) executable;
                ScriptExecutor<T, S> scriptExecutor = sc.getScriptExecutor();
                XmlComponentVisitor.this.scriptName = scriptExecutor.getName();
                XmlComponentVisitor.this.scriptType = scriptExecutor.getType();
                XmlComponentVisitor.this.script = scriptExecutor.getScript();

                // Ensure executable name is scriptExecutor's name.
                XmlComponentVisitor.this.executable = scriptExecutor.getName();

                ScriptExecutor<T, S> rollbackExecutor = sc.getRollbackExecutor();
                if (rollbackExecutor != null) {
                    XmlComponentVisitor.this.rollbackScriptName = rollbackExecutor.getName();
                    XmlComponentVisitor.this.rollbackScriptType = rollbackExecutor.getType();
                    XmlComponentVisitor.this.rollbackScript = rollbackExecutor.getScript();
                }
            } else {
                String phrase = ExecutableTypeDetector.get().getPhrase(executable.getClass());
                if (AuxiliaryUtils.isNotBlank(phrase)) {
                    XmlComponentVisitor.this.executable = phrase;
                }
            }
        }
    }
}
