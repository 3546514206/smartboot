package org.smartboot.flow.spring.extension;

import org.smartboot.flow.core.attribute.AttributeHolder;
import org.smartboot.flow.core.attribute.Attributes;
import org.smartboot.flow.core.executable.DecorateExecutable;
import org.smartboot.flow.core.parser.DefinitionVisitor;
import org.smartboot.flow.core.parser.ElementAttr;
import org.smartboot.flow.core.parser.ParserContext;
import org.smartboot.flow.core.parser.ThreadPoolCreator;
import org.smartboot.flow.core.parser.definition.AdapterDefinition;
import org.smartboot.flow.core.parser.definition.ChooseDefinition;
import org.smartboot.flow.core.parser.definition.ComponentDefinition;
import org.smartboot.flow.core.parser.definition.ConditionDefinition;
import org.smartboot.flow.core.parser.definition.ElementDefinition;
import org.smartboot.flow.core.parser.definition.EngineDefinition;
import org.smartboot.flow.core.parser.definition.FlowDefinition;
import org.smartboot.flow.core.parser.definition.IfElementDefinition;
import org.smartboot.flow.core.parser.definition.PipelineComponentDefinition;
import org.smartboot.flow.core.parser.definition.PipelineDefinition;
import org.smartboot.flow.core.parser.definition.ScriptDefinition;
import org.smartboot.flow.core.script.ScriptCondition;
import org.smartboot.flow.core.script.ScriptExecutable;
import org.smartboot.flow.core.script.ScriptExecutor;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author qinluo
 * @date 2022/11/17 0:01
 * @since 1.0.0
 */
public class BeanDefinitionVisitor implements DefinitionVisitor, BeanFactoryAware {

    public static final String NAME = BeanDefinitionVisitor.class.getName();

    private ParserContext context;

    /**
     * Temporary def register.
     */
    private BeanDefinitionRegister register;

    public BeanDefinitionVisitor() {
    }

    public BeanDefinitionVisitor(BeanDefinitionRegistry registry, ParserContext context) {
        AssertUtil.notNull(registry, "registry must not be null!");
        this.context = context;
        this.register = new BeanDefinitionRegister(registry);
    }

    @Override
    public void init(ParserContext context) {
        this.context = context;
        AssertUtil.notNull(register, "registry must not be null!");
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (beanFactory instanceof BeanDefinitionRegistry) {
            this.register = new BeanDefinitionRegister((BeanDefinitionRegistry) beanFactory);
        }
    }

    @Override
    public void visit(ScriptDefinition sed) {
        Class<?> javaType = sed.getJavaType();
        // Maybe a ref.
        if (javaType == null) {
            BeanDefinition beanDefinition = register.getBeanDefinition(sed.getName());
            AssertUtil.notNull(beanDefinition, "Could not found ScriptExecutor " + sed.getName() + " in spring container");
            String beanClassName = beanDefinition.getBeanClassName();
            Class<?> defJavaType = AuxiliaryUtils.asClass(beanClassName);
            AssertUtil.notNull(defJavaType, "bean " + sed.getName() + " javaType must be subclass of ScriptExecutor");
            AssertUtil.isTrue(ScriptExecutor.class.isAssignableFrom(defJavaType), "bean " + sed.getName() + " javaType must be subclass of ScriptExecutor");

            // Register script content.
            beanDefinition.getPropertyValues().add("script", sed.getScript());

            if (AuxiliaryUtils.isNotBlank(sed.getName())) {
                beanDefinition.getPropertyValues().add("name", sed.getName());
            }
        } else {
            RootBeanDefinition definition = asSpringDefinition(sed);
            PropertyValue script = new PropertyValue("script", sed.getScript());
            definition.getPropertyValues().addPropertyValue(script);
            register.registerBeanDefinition(sed.getIdentifier(), definition, true);
        }
    }

    @Override
    public void visit(EngineDefinition ed) {
        RootBeanDefinition definition = asSpringDefinition(ed);
        PropertyValue pipeline = new PropertyValue("pipeline", new RuntimeBeanReference(ed.getPipeline()));

        definition.getPropertyValues().addPropertyValue(pipeline);
        definition.setInitMethodName("validate");

        if (ed.getThreadpools().size() > 0) {
            definition.getPropertyValues().addPropertyValue("executor", ThreadPoolCreator.create(ed.getThreadpools()));
        }

        if (AuxiliaryUtils.isNotBlank(ed.getExceptionHandler())) {
            if (AuxiliaryUtils.isType(ed.getExceptionHandler())) {
                RootBeanDefinition handlerDef = new RootBeanDefinition();
                handlerDef.setBeanClassName(ed.getExceptionHandler());
                definition.getPropertyValues().addPropertyValue("exceptionHandler", handlerDef);
            } else {
                // 可能是spring bean
                definition.getPropertyValues().addPropertyValue("exceptionHandler", new RuntimeBeanReference(ed.getExceptionHandler()));
            }
        }

        register.registerBeanDefinition(ed.getIdentifier(), definition, true);
    }

    @Override
    public void visit(PipelineDefinition ed) {
        RootBeanDefinition definition = asSpringDefinition(ed);

        ManagedList<Object> components = new ManagedList<>();
        definition.getPropertyValues().add("components", components);

        ed.getChildren().forEach(p -> p.visit(this));

        for (ComponentDefinition elementDef : ed.getChildren()) {
            // Fire sub visit.
            elementDef.visit(this);

            BeanDefinition beanDefinition = register.getBeanDefinition(elementDef.getIdentifier());
            AssertUtil.notNull(beanDefinition, elementDef.getIdentifier() + " not exist");
            components.add(beanDefinition);
        }

        register.registerBeanDefinition(ed.getIdentifier(), definition, !AuxiliaryUtils.isAnonymous(ed.getIdentifier()));
    }

    @Override
    public void visit(PipelineComponentDefinition ed) {
        RootBeanDefinition definition = asSpringDefinition(ed);
        PropertyValue pipeline = new PropertyValue("pipeline", new RuntimeBeanReference(ed.getPipeline()));

        if (AuxiliaryUtils.isAnonymous(ed.getPipeline())) {
            context.getRegistered(ed.getPipeline()).visit(this);
            definition.getPropertyValues().add("pipeline", register.getBeanDefinition(ed.getPipeline()));
        } else {
            definition.getPropertyValues().addPropertyValue(pipeline);
        }

        appendAttributes(ed, definition);
        register.registerBeanDefinition(ed.getIdentifier(), definition);
    }

    private void appendAttributes(ComponentDefinition ed, RootBeanDefinition definition) {
        List<AttributeHolder> attributes = ed.getAttributes();

        for (AttributeHolder holder : attributes) {
            Attributes attribute = holder.getAttribute();
            if (attribute == Attributes.NAME
                    || attribute == Attributes.DEGRADE_CALLBACK
                    || attribute == Attributes.DEPENDS_ALL
                    || !attribute.isVisible()) {
                continue;
            }

            PropertyValue value = new PropertyValue(attribute.getName(), holder.getValue());
            definition.getPropertyValues().addPropertyValue(value);
        }

        definition.getPropertyValues().add("attributes", ed.getAttributes());

        if (ed.getAttributeMap().size() > 0) {
            definition.getPropertyValues().add("attributeMap", ed.getAttributeMap());
        }
    }

    private RootBeanDefinition decorateIfNecessary(RootBeanDefinition originDef, ElementDefinition ed) {
        if (ed.getBindingAttrs().size() == 0) {
            return originDef;
        }

        RootBeanDefinition decorateDef = new RootBeanDefinition();
        decorateDef.setBeanClass(DecorateExecutable.class);
        decorateDef.getPropertyValues().add("delegate", originDef);
        decorateDef.getPropertyValues().add("bindingAttrs", ed.getBindingAttrs().stream().collect(Collectors.toMap(ElementAttr::getName, ElementAttr::getValue)));

        // binding attributes.
        for (ElementAttr attr : ed.getBindingAttrs()) {
            String fieldName = attr.getName().substring(ed.getBindingAttrPrefix().length());
            fieldName = AuxiliaryUtils.transfer2CamelCase(fieldName);
            originDef.getPropertyValues().add(fieldName, maybeReference(attr.getValue()));
        }

        return decorateDef;
    }

    private Object maybeReference(String value) {
        if (value == null) {
            return null;
        }

        // maybe java type
        Class<?> javaType = AuxiliaryUtils.asClass(value);
        if (javaType != null) {
            RootBeanDefinition def = new RootBeanDefinition();
            def.setBeanClass(javaType);
            return def;
        }

        // maybe bean beanName
        try {
            if (register.getBeanDefinition(value) != null) {
                return new RuntimeBeanReference(value.trim());
            }
        } catch (NoSuchBeanDefinitionException ignored) {

        }


        return value;
    }

    @Override
    public void visit(ElementDefinition ed) {
        RootBeanDefinition definition = asSpringDefinition(ed);

        String execute = ed.getExecute();

        if (AuxiliaryUtils.isType(execute)) {
            RootBeanDefinition conditionDef = new RootBeanDefinition();
            conditionDef.setBeanClassName(execute);
            definition.getPropertyValues().add("executable", decorateIfNecessary(conditionDef, ed));
        } else if (ed.getContext().getRegistered(execute) instanceof ScriptDefinition) {
            ed.getContext().getRegistered(execute).visit(this);
            BeanDefinition beanDefinition = register.getBeanDefinition(execute);
            Class<?> beanType = beanDefinition != null ? AuxiliaryUtils.asClass(beanDefinition.getBeanClassName()) : null;
            if (beanType != null && ScriptExecutor.class.isAssignableFrom(beanType)) {
                RootBeanDefinition rbd = new RootBeanDefinition();
                rbd.setBeanClass(ScriptExecutable.class);
                rbd.getPropertyValues().addPropertyValue("scriptExecutor", register.isSpringBean(execute) ? new RuntimeBeanReference(execute) : beanDefinition);

                String rollbackName = getRollbackScriptName(execute);

                if (ed.getContext().getRegistered(rollbackName) instanceof ScriptDefinition) {
                    ed.getContext().getRegistered(rollbackName).visit(this);
                    BeanDefinition rollbackRbd = register.getBeanDefinition(execute);
                    beanType = rollbackRbd != null ? AuxiliaryUtils.asClass(rollbackRbd.getBeanClassName()) : null;
                    if (beanType != null && ScriptExecutor.class.isAssignableFrom(beanType)) {
                        rbd.getPropertyValues().addPropertyValue("rollbackExecutor", register.isSpringBean(rollbackName) ? new RuntimeBeanReference(rollbackName) : rollbackRbd);
                    }
                }
                definition.getPropertyValues().add("executable", rbd);
            } else {
                definition.getPropertyValues().add("executable", new RuntimeBeanReference(execute));
            }
        } else {
            definition.getPropertyValues().add("executable", new RuntimeBeanReference(execute));
        }

        AttributeHolder degradeCallback = ed.getAttributes().stream()
                .filter(p -> p.getAttribute() == Attributes.DEGRADE_CALLBACK).findFirst().orElse(null);

        if (degradeCallback != null) {
            String callback = String.valueOf(degradeCallback.getValue());

            if (AuxiliaryUtils.isType(callback)) {
                RootBeanDefinition conditionDef = new RootBeanDefinition();
                conditionDef.setBeanClassName(callback);
                definition.getPropertyValues().add("degradeCallback", conditionDef);
            } else {
                AssertUtil.notBlank(callback, "component callback ref must not be null!");
                definition.getPropertyValues().add("degradeCallback", new RuntimeBeanReference(callback));
            }
        }

        appendAttributes(ed, definition);
        register.registerBeanDefinition(ed.getIdentifier(), definition);
    }

    @Override
    public void visit(IfElementDefinition ed) {
        RootBeanDefinition definition = asSpringDefinition(ed);

        processCondition(ed, definition);

        ed.getIfThenRef().visit(this);
        BeanDefinition beanDefinition = register.getBeanDefinition(ed.getIfThenRef().getIdentifier());
        AssertUtil.notNull(beanDefinition, ed.getIfThenRef().getIdentifier() + " not exist");
        definition.getPropertyValues().add("thenComponent", beanDefinition);

        if (ed.getIfElseRef() != null) {
            ed.getIfElseRef().visit(this);
            beanDefinition = register.getBeanDefinition(ed.getIfElseRef().getIdentifier());
            AssertUtil.notNull(beanDefinition, ed.getIfElseRef().getIdentifier() + " not exist");
            definition.getPropertyValues().add("elseComponent", beanDefinition);
        }

        appendAttributes(ed, definition);
        register.registerBeanDefinition(ed.getIdentifier(), definition);
    }

    @Override
    public void visit(ChooseDefinition ed) {
        RootBeanDefinition definition = asSpringDefinition(ed);
        definition.getPropertyValues().add("allBranchWasString", true);

        processCondition(ed, definition);

        ManagedMap<String, Object> managedMap = new ManagedMap<>();
        Map<String, ComponentDefinition> caseMap = ed.getCaseMap();
        for (Map.Entry<String, ComponentDefinition> entry : caseMap.entrySet()) {
            entry.getValue().visit(this);
            BeanDefinition beanDefinition = register.getBeanDefinition(entry.getValue().getIdentifier());
            AssertUtil.notNull(beanDefinition, "case " + entry.getKey() + " " + ed.getDefaultDef().getIdentifier() + " not exist");
            managedMap.put(entry.getKey(), beanDefinition);
        }

        definition.getPropertyValues().add("branches", managedMap);

        if (ed.getDefaultDef() != null) {
            ed.getDefaultDef().visit(this);
            BeanDefinition beanDefinition = register.getBeanDefinition(ed.getDefaultDef().getIdentifier());
            AssertUtil.notNull(beanDefinition, ed.getDefaultDef().getIdentifier() + " not exist");
            definition.getPropertyValues().add("defaultBranch", beanDefinition);
        }

        appendAttributes(ed, definition);
        register.registerBeanDefinition(ed.getIdentifier(), definition);
    }

    private void processCondition(ConditionDefinition ed, RootBeanDefinition definition) {
        String test = ed.getTest();

        if (AuxiliaryUtils.isType(test)) {
            RootBeanDefinition conditionDef = new RootBeanDefinition();
            conditionDef.setBeanClassName(test);
            definition.getPropertyValues().add("condition", conditionDef);
        } else if (ed.getContext().getRegistered(test) instanceof ScriptDefinition) {
            ed.getContext().getRegistered(test).visit(this);
            BeanDefinition beanDefinition = register.getBeanDefinition(test);
            Class<?> beanType = beanDefinition != null ? AuxiliaryUtils.asClass(beanDefinition.getBeanClassName()) : null;
            if (beanType != null && ScriptExecutor.class.isAssignableFrom(beanType)) {
                RootBeanDefinition rbd = new RootBeanDefinition();
                rbd.setBeanClass(ScriptCondition.class);
                rbd.getPropertyValues().addPropertyValue("scriptExecutor", register.isSpringBean(test) ? new RuntimeBeanReference(test) : beanDefinition);
                definition.getPropertyValues().add("condition", rbd);
            } else {
                definition.getPropertyValues().add("condition", new RuntimeBeanReference(test));
            }
        } else {
            definition.getPropertyValues().add("condition", new RuntimeBeanReference(test));
        }
    }

    @Override
    public void visit(AdapterDefinition ed) {
        RootBeanDefinition definition = asSpringDefinition(ed);

        if (AuxiliaryUtils.isType(ed.getExecute())) {
            RootBeanDefinition adapterDef = new RootBeanDefinition();
            adapterDef.setBeanClassName(ed.getExecute());
            definition.getPropertyValues().add("adapter", adapterDef);
        } else {
            definition.getPropertyValues().add("adapter", new RuntimeBeanReference(ed.getExecute()));
        }

        ed.getPipelineElement().visit(this);

        BeanDefinition beanDefinition = register.getBeanDefinition(ed.getPipelineElement().getIdentifier());
        definition.getPropertyValues().add("component", beanDefinition);
        appendAttributes(ed, definition);
        register.registerBeanDefinition(ed.getIdentifier(), definition);

    }

    private RootBeanDefinition asSpringDefinition(FlowDefinition ed) {
        RootBeanDefinition definition = new RootBeanDefinition();
        PropertyValue name = new PropertyValue("name", ed.getName());
        definition.setBeanClass(ed.resolveType());
        definition.getPropertyValues().addPropertyValue(name);

        if (ed instanceof ComponentDefinition) {
            definition.getPropertyValues().add("valueResolver", new RuntimeBeanReference(SpringAttributeValueResolver.class.getName()));
        }
        return definition;
    }
}
