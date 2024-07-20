package org.smartboot.flow.core.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.Adapter;
import org.smartboot.flow.core.Condition;
import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.core.Pipeline;
import org.smartboot.flow.core.attribute.AttributeValueResolver;
import org.smartboot.flow.core.builder.AdapterBuilder;
import org.smartboot.flow.core.builder.Builders;
import org.smartboot.flow.core.builder.ChooseBuilder;
import org.smartboot.flow.core.builder.EngineBuilder;
import org.smartboot.flow.core.builder.PipelineBuilder;
import org.smartboot.flow.core.component.AdapterComponent;
import org.smartboot.flow.core.component.ChooseComponent;
import org.smartboot.flow.core.component.Component;
import org.smartboot.flow.core.component.PipelineComponent;
import org.smartboot.flow.core.exception.ExceptionHandler;
import org.smartboot.flow.core.executable.DecorateExecutable;
import org.smartboot.flow.core.executable.Executable;
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
import org.smartboot.flow.core.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author qinluo
 * @date 2022-11-15 20:17:18
 * @since 1.0.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class BuilderDefinitionVisitor implements DefinitionVisitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(BuilderDefinitionVisitor.class);
    private static final String SETTER_PREFIX = "set";

    private final Map<String, Component<?, ?>> namedComponents = new ConcurrentHashMap<>();
    private final Map<String, Pipeline> assembledPipelines = new ConcurrentHashMap<>();
    private final Map<String, PipelineBuilder<?, ?>> namedPipelines = new ConcurrentHashMap<>();
    private final Map<String, EngineBuilder<?, ?>> namedEngines = new ConcurrentHashMap<>();
    private final Map<String, FlowEngine<?, ?>> cachedEngines = new ConcurrentHashMap<>();
    private final Map<String, List<PipelineEndCallBack>> callbacks = new ConcurrentHashMap<>();
    private final Map<String, ScriptExecutor> scriptExecutors = new ConcurrentHashMap<>();
    private final ObjectCreator objectCreator;
    private final boolean useCache;
    private final AttributeValueResolver valueResolver;

    public BuilderDefinitionVisitor(boolean useCache, ObjectCreator objectCreator, AttributeValueResolver resolver) {
        this.useCache = useCache;
        this.objectCreator = objectCreator;
        this.valueResolver = resolver;
    }

    @Override
    public void visit(ScriptDefinition sed) {
        ScriptExecutor scriptExecutor = Builders.script()
                .objectCreator(objectCreator)
                .name(sed.getName()).script(sed.getScript()).type(sed.getType()).build();
        scriptExecutors.put(sed.getIdentifier(), scriptExecutor);
    }

    @Override
    public void visit(PipelineDefinition ed) {
        PipelineBuilder pipelineBuilder = namedPipelines.get(ed.getIdentifier());
        if (pipelineBuilder == null) {
            pipelineBuilder = Builders.pipeline().name(ed.getIdentifier());
        }
        namedPipelines.put(ed.getIdentifier(), pipelineBuilder);
        List<ComponentDefinition> children = ed.getChildren();
        for (ComponentDefinition child : children) {
            child.visit(this);
        }

        for (ComponentDefinition child : children) {
            pipelineBuilder.next(this.namedComponents.get(child.getIdentifier()));
        }

        Pipeline<?, ?> build = pipelineBuilder.build();
        invoke(ed.getIdentifier(), build);
        this.assembledPipelines.put(ed.getIdentifier(), build);
    }

    @Override
    public void visit(EngineDefinition ed) {
        String engineName = ed.getIdentifier();
        EngineBuilder<?, ?> engineBuilder = Builders.engine().name(engineName);

        PipelineBuilder pipelineBuilder = this.namedPipelines.get(ed.getPipeline());
        if (pipelineBuilder == null) {
            pipelineBuilder = Builders.pipeline().name(ed.getPipeline());
            this.namedPipelines.put(ed.getPipeline(), pipelineBuilder);
            this.collect(ed.getPipeline(), (engineBuilder::pipeline));
        }  else if (assembledPipelines.get(ed.getPipeline()) != null) {
            engineBuilder.pipeline(assembledPipelines.get(ed.getPipeline()));
        } else {
            this.collect(ed.getPipeline(), (engineBuilder::pipeline));
        }

        if (ed.getThreadpools().size() > 0) {
            engineBuilder.executor(ThreadPoolCreator.create(ed.getThreadpools()));
        }

        if (AuxiliaryUtils.isNotBlank(ed.getExceptionHandler())) {
            engineBuilder.handler(newInstance(ed.getExceptionHandler(), ExceptionHandler.class));
        }

        this.namedEngines.put(engineName, engineBuilder);
    }

    @Override
    public void visit(PipelineComponentDefinition ed) {
        String engineName = ed.getIdentifier();
        PipelineBuilder<?,?> pipelineBuilder = this.namedPipelines.get(ed.getPipeline());

        PipelineComponent pipelineComponent = Builders.pipelineRef()
                                                .delayPipeline(true)
                                                .apply(ed.getAttributes())
                                                .build();

        if (pipelineBuilder == null) {
            pipelineBuilder = Builders.pipeline().name(ed.getPipeline());
            this.namedPipelines.put(ed.getPipeline(), pipelineBuilder);
            this.collect(ed.getPipeline(), pipelineComponent::setPipeline);
        } else if (assembledPipelines.get(ed.getPipeline()) != null) {
            pipelineComponent.setPipeline(assembledPipelines.get(ed.getPipeline()));
        } else {
            this.collect(ed.getPipeline(), (pipelineComponent::setPipeline));
        }
        pipelineComponent.setAttributeMap(ed.getAttributeMap());
        pipelineComponent.setAttributes(ed.getAttributes());
        this.namedComponents.put(engineName, pipelineComponent);
    }

    private void collect(String name, PipelineEndCallBack callBack) {
        List<PipelineEndCallBack> pipelineEndCallBacks = this.callbacks.getOrDefault(name, new ArrayList<>(0));
        pipelineEndCallBacks.add(callBack);
        this.callbacks.put(name, pipelineEndCallBacks);
    }

    private void invoke(String name, Pipeline<?, ?> pipeline) {
        List<PipelineEndCallBack> cbs = this.callbacks.getOrDefault(name, new ArrayList<>());
        cbs.forEach(p -> p.execute(pipeline));
    }

    @Override
    public void visit(ElementDefinition ed) {
        Executable executable;
        if (AuxiliaryUtils.isType(ed.getExecute())) {
            executable = newInstance(ed.getExecute(), Executable.class);
        } else if (ed.getContext().getRegistered(ed.getExecute()) instanceof ScriptDefinition) {
            executable = new ScriptExecutable();
            ScriptExecutor scriptExecutor = getScriptExecutor(ed.getExecute(), ed.getContext());
            AssertUtil.notNull(scriptExecutor, "script executor " + ed.getExecute() + " is null");
            ((ScriptExecutable)executable).setScriptExecutor(scriptExecutor);

            String rollback = getRollbackScriptName(ed.getExecute());
            if (ed.getContext().getRegistered(rollback) instanceof ScriptDefinition) {
                scriptExecutor = getScriptExecutor(rollback, ed.getContext());
                AssertUtil.notNull(scriptExecutor, "script executor is null");
                ((ScriptExecutable)executable).setRollbackExecutor(scriptExecutor);
            }
        } else {
            executable = newInstance(ed.getExecute(), Executable.class);
        }

        AssertUtil.notNull(executable, "executable " + executable + " is null");
        executable = decorateIfNecessary(executable, ed);

        Component<?, ?> component = Builders.executable()
                                            .executable(executable)
                                            .withResolver(valueResolver)
                                            .apply(ed.getAttributes())
                                            .build();
        component.setAttributes(ed.getAttributes());
        component.setAttributeMap(ed.getAttributeMap());
        namedComponents.put(ed.getIdentifier(), component);
    }

    private Executable decorateIfNecessary(Executable executable, ElementDefinition ed) {
        if (ed.getBindingAttrs().size() == 0) {
            return executable;
        }

        DecorateExecutable decorate = new DecorateExecutable();
        decorate.setDelegate(executable);
        decorate.setBindingAttrs(ed.getBindingAttrs().stream().collect(Collectors.toMap(ElementAttr::getName, ElementAttr::getValue)));

        // binding attributes.
        for (ElementAttr attr : ed.getBindingAttrs()) {
            String fieldName = attr.getName().substring(ed.getBindingAttrPrefix().length());
            fieldName = AuxiliaryUtils.transfer2CamelCase(fieldName);

            String methodName = SETTER_PREFIX + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

            Method m = ReflectionUtils.lookUpMethod(executable.getClass(), methodName,  (method) -> method.getParameterCount() == 1);
            if (m != null) {
                Object value = valueResolver.resolve(m.getParameterTypes()[0], attr.getValue());
                ReflectionUtils.invokeMethod(executable, m, new Object[] { value });
                continue;
            }
            Field f = ReflectionUtils.lookupField(executable.getClass(), fieldName);
            if (f != null) {
                Object value = valueResolver.resolve(f.getType(), attr.getValue());
                ReflectionUtils.setField(executable, f, value);
                continue;
            }
            LOGGER.warn("field {} of value {} failed to set in executable {}", attr.getName(), attr.getValue(), executable.getClass().getName());
        }

        return decorate;
    }

    private <T> T newInstance(String type, Class<T> expectType) {
        return objectCreator.create(type, expectType, useCache);
    }

    @Override
    public void visit(IfElementDefinition ed) {
        Condition condition = getCondition(ed);
        AssertUtil.notNull(condition, "can't find condition for if-element, test = " + ed.getTest());

        ed.getIfThenRef().visit(this);
        if (ed.getIfElseRef() != null) {
            ed.getIfElseRef().visit(this);
        }

        Component then = this.namedComponents.get(ed.getIfThenRef().getIdentifier());
        Component elseBranch = null;
        if (ed.getIfElseRef() != null) {
            elseBranch = this.namedComponents.get(ed.getIfElseRef().getIdentifier());
        }
        Component<?, ?> build = Builders.newIf().test(condition).withResolver(valueResolver)
                                .apply(ed.getAttributes()).then(then).otherwise(elseBranch).build();
        build.setAttributeMap(ed.getAttributeMap());
        build.setAttributes(ed.getAttributes());
        this.namedComponents.put(ed.getIdentifier(), build);
    }

    private Condition getCondition(ConditionDefinition ed) {
        String test = ed.getTest();
        Condition condition;
        if (AuxiliaryUtils.isType(test)) {
            condition = newInstance(test, Condition.class);
        } else if (ed.getContext().getRegistered(test) instanceof ScriptDefinition) {
            ScriptExecutor scriptExecutor = getScriptExecutor(test, ed.getContext());
            AssertUtil.notNull(scriptExecutor, "script executor is null");
            condition = new ScriptCondition();
            ((ScriptCondition)condition).setScriptExecutor(scriptExecutor);
        } else {
            condition = newInstance(test, Condition.class);
        }
        return condition;
    }

    public ScriptExecutor getScriptExecutor(String test, ParserContext context) {
        FlowDefinition registered = context.getRegistered(test);
        AssertUtil.notNull(registered, "registered condition def[" + test + "] not found");
        this.visit(registered);
        return scriptExecutors.get(test);
    }

    @Override
    public void visit(ChooseDefinition ed) {
        Condition condition = getCondition(ed);
        AssertUtil.notNull(condition, "can't find condition for choose-element, test = " + ed.getTest());

        Map<String, ComponentDefinition> chooseCaseList = ed.getCaseMap();
        ComponentDefinition chooseDefaultRef = ed.getDefaultDef();

        for (Map.Entry<String, ComponentDefinition> entry : chooseCaseList.entrySet()) {
            entry.getValue().visit(this);
        }

        if (chooseDefaultRef != null) {
            chooseDefaultRef.visit(this);
        }

        ChooseBuilder chooseBuilder = Builders.newChoose()
                .test(condition)
                .withResolver(valueResolver)
                .apply(ed.getAttributes());

        for (Map.Entry<String, ComponentDefinition> entry : chooseCaseList.entrySet()) {
            chooseBuilder.when(entry.getKey()).then(this.namedComponents.get(entry.getValue().getIdentifier()));
        }

        if (chooseDefaultRef != null) {
            chooseBuilder.defaultBranch(this.namedComponents.get(chooseDefaultRef.getIdentifier()));
        }

        ChooseComponent<?, ?> build = chooseBuilder.build();
        build.setAllBranchWasString(true);
        build.setAttributeMap(ed.getAttributeMap());
        build.setAttributes(ed.getAttributes());
        this.namedComponents.put(ed.getIdentifier(), build);
    }

    @Override
    public void visit(AdapterDefinition ed) {
        String execute = ed.getExecute();
        Adapter adapter = newInstance(execute, Adapter.class);
        AssertUtil.notNull(adapter, "can't find adapter , execute=" + execute);

        ed.getPipelineElement().visit(this);
        Component<?, ?> component = namedComponents.get(ed.getPipelineElement().getIdentifier());
        AssertUtil.notNull(component, "adapter's component [" + ed.getPipelineElement().getIdentifier() + "] must not be null!");

        AdapterBuilder adapterBuilder = Builders.newAdapter()
                .withResolver(valueResolver)
                .adapter(adapter)
                .apply(ed.getAttributes())
                .component(component);

        AdapterComponent adapterComponent = adapterBuilder.build();
        adapterComponent.setAttributeMap(ed.getAttributeMap());
        adapterComponent.setAttributes(ed.getAttributes());
        this.namedComponents.put(ed.getIdentifier(), adapterComponent);
    }

    /**
     * Return all parsed engine names.
     *
     * @return engine names.
     */
    public List<String> getEngineNames() {
        return new ArrayList<>(namedEngines.keySet());
    }

    public <T, S> FlowEngine<T, S> getEngine(String name) {
        FlowEngine<?, ?> flowEngine = cachedEngines.get(name);
        if (flowEngine != null) {
            return (FlowEngine<T, S>)flowEngine;
        }

        EngineBuilder<?, ?> engineBuilder = namedEngines.get(name);
        flowEngine = engineBuilder.build();
        cachedEngines.put(name, flowEngine);
        return (FlowEngine<T, S>)flowEngine;
    }

    /**
     * Pipeline组装后置通知
     */
    public interface PipelineEndCallBack {

        void execute(Pipeline pipeline);
    }
}
