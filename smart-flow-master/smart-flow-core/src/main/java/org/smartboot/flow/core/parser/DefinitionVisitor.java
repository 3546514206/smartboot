package org.smartboot.flow.core.parser;

import org.smartboot.flow.core.parser.definition.AdapterDefinition;
import org.smartboot.flow.core.parser.definition.ChooseDefinition;
import org.smartboot.flow.core.parser.definition.ElementDefinition;
import org.smartboot.flow.core.parser.definition.EngineDefinition;
import org.smartboot.flow.core.parser.definition.FlowDefinition;
import org.smartboot.flow.core.parser.definition.IfElementDefinition;
import org.smartboot.flow.core.parser.definition.PipelineComponentDefinition;
import org.smartboot.flow.core.parser.definition.PipelineDefinition;
import org.smartboot.flow.core.parser.definition.ScriptDefinition;

/**
 * @author qinluo
 * @date 2022-11-15 20:15:34
 * @since 1.0.0
 */
public interface DefinitionVisitor {

    /**
     * Init visitor with parse ctx.
     *
     * @param ctx ctx.
     * @since 1.0.4
     */
    default void init(ParserContext ctx) {

    }

    /**
     * Visit element definition.
     *
     * @param ed element definition.
     */
    default void visit(FlowDefinition ed) {
        ed.visit(this);
    }

    /**
     * Visit engine element.
     *
     * @param ed element definition.
     */
    void visit(EngineDefinition ed);

    /**
     * Visit pipeline element
     *
     * @param ed element definition.
     */
    void visit(PipelineDefinition ed);

    /**
     * Visit adapter definition
     *
     * @param adapterDefinition def
     */
    void visit(AdapterDefinition adapterDefinition);

    /**
     * Visit subprocess definition.
     *
     * @param ed subprocess def.
     */
    void visit(PipelineComponentDefinition ed);

    /**
     * Visit if branch definition.
     *
     * @param ed if def.
     */
    void visit(IfElementDefinition ed);

    /**
     * Visit choose branch definition.
     *
     * @param ed ed.
     */
    void visit(ChooseDefinition ed);

    /**
     * Visit basic component.
     *
     * @param ed basic component.
     */
    void visit(ElementDefinition ed);

    /**
     * Visit script definition.
     *
     * @since 1.1.0
     * @param ed script definition
     */
    void visit(ScriptDefinition ed);

    /**
     * Return rollback script name of name.
     *
     * <code>
     *     initScript to initScript-rollback.
     *     reduceStock to reduceStock-rollback.
     *
     * </code>
     *
     * @param name name
     * @return     rollback name
     */
    default String getRollbackScriptName(String name) {
        return name + "-rollback";
    }
}
