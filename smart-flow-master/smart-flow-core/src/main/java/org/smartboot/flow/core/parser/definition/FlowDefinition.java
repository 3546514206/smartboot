package org.smartboot.flow.core.parser.definition;


import org.smartboot.flow.core.Validator;
import org.smartboot.flow.core.common.Uniqueness;
import org.smartboot.flow.core.parser.DefinitionVisitor;
import org.smartboot.flow.core.parser.ParserContext;

/**
 * 所有定义抽象类
 *
 * @author qinluo
 * @date 2023-06-17 10:16:02
 * @since 1.1.0
 */
public abstract class FlowDefinition extends Uniqueness implements Validator {

    /**
     * Avoid cycle invoke.
     */
    protected volatile boolean visitCalled = false;
    protected String name;
    protected ParserContext context;

    @Override
    public void validate() {

    }

    public void visit(DefinitionVisitor visitor) {
        if (this.visitCalled) {
            return;
        }

        this.visitCalled = true;
        this.doVisit(visitor);
    }

    /**
     * Execute visit by subclass.
     *
     * @param visitor visitor.
     */
    public abstract void doVisit(DefinitionVisitor visitor);

    /**
     * 返回当前定义class type.
     *
     * @return class type.
     */
    public abstract Class<?> resolveType();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ParserContext getContext() {
        return context;
    }

    public void setContext(ParserContext context) {
        this.context = context;
    }
}
