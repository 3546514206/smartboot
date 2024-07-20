package org.smartboot.flow.core;

import org.smartboot.flow.core.visitor.ConditionVisitor;

/**
 * @author qinluo
 * @date 2022-11-12 21:34:30
 * @since 1.0.0
 */
public abstract class Condition<T, S> implements Describable {

    public Object test(EngineContext<T, S> context) {
        return this.test(context.getReq(), context.getResult());
    }

    public Object test(T t, S s) {
        return null;
    }

    /**
     *  Visit condition's structure
     */
    public void visit(ConditionVisitor visitor) {
        visitor.visitSource(this);
    }
}
