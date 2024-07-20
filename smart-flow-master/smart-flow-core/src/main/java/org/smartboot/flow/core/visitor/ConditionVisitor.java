package org.smartboot.flow.core.visitor;

import org.smartboot.flow.core.Condition;

/**
 * @author qinluo
 * @date 2022-11-13 22:34:58
 * @since 1.0.5
 */
public class ConditionVisitor {

    protected ConditionVisitor delegate;

    public ConditionVisitor() {
        this(null);
    }

    public ConditionVisitor(ConditionVisitor delegate) {
        this.delegate = delegate;
    }

    /**
     * Visit condition source.
     */
    public <T, S> void visitSource(Condition<T, S> condition) {
        if (delegate != null) {
            delegate.visitSource(condition);
        }
    }
}
