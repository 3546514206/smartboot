package org.smartboot.flow.core.parser.definition;

import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.core.parser.DefinitionVisitor;
import org.smartboot.flow.core.parser.ElementAttr;
import org.smartboot.flow.core.util.AssertUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qinluo
 * @date 2022-11-16 12:35:02
 * @since 1.0.0
 */
public class EngineDefinition extends FlowDefinition {

    /**
     * pipeline name.
     */
    private String pipeline;
    private final List<ElementAttr> threadpools = new ArrayList<>();
    private String exceptionHandler;

    public String getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(String exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public String getPipeline() {
        return this.pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }

    public List<ElementAttr> getThreadpools() {
        return threadpools;
    }

    @Override
    public void validate() {
        AssertUtil.notNull(name, "engine's name must not be null");
        AssertUtil.notNull(pipeline, "engine's name must not be null");
        AssertUtil.notNull(context.getRegistered(pipeline), "pipeline " + pipeline + " not exist in " + name);
    }

    @Override
    public void doVisit(DefinitionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Class<?> resolveType() {
        return FlowEngine.class;
    }
}
