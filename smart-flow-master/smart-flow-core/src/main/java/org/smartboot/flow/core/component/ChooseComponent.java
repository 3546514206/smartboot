package org.smartboot.flow.core.component;


import org.smartboot.flow.core.Condition;
import org.smartboot.flow.core.EngineContext;
import org.smartboot.flow.core.Key;
import org.smartboot.flow.core.common.ComponentType;
import org.smartboot.flow.core.invoker.WrappedInvoker;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.visitor.ComponentVisitor;
import org.smartboot.flow.core.visitor.ConditionVisitor;

import java.util.Map;

/**
 * @author qinluo
 * @date 2022-11-12 18:57:40
 * @since 1.0.0
 */
public class ChooseComponent<T, S> extends Component<T, S> {

    private Map<Object, Component<T, S>> branches;
    private Condition<T, S> condition;
    private Component<T, S> defaultBranch;
    private boolean allBranchWasString;

    public void setBranches(Map<Object, Component<T, S>> branches) {
        this.branches = branches;
    }

    public void setCondition(Condition<T, S> condition) {
        this.condition = condition;
    }

    public void setDefaultBranch(Component<T, S> defaultBranch) {
        this.defaultBranch = defaultBranch;
    }

    public void setAllBranchWasString(boolean allBranchWasString) {
        this.allBranchWasString = allBranchWasString;
    }

    @Override
    public int invoke(EngineContext<T, S> context) throws Throwable {
        Object branch = condition.test(context);
        Component<T, S> execute = null;

        // Compatible
        if (branch != null && allBranchWasString) {
            branch = String.valueOf(branch);
        }

        if (branch != null && branches.containsKey(branch)) {
            execute = branches.get(branch);
        } else if (defaultBranch != null) {
            execute = defaultBranch;
            branch = "default";
        }

        if (execute != null) {
            context.putExt(Key.of(this), execute);
            context.enter("branch##" + branch);
            try {
                WrappedInvoker.invoke(context, execute);
            } finally {
                context.exit("branch##" + branch);
            }
        }

        return 1;
    }

    @Override
    public boolean isRollbackable(EngineContext<T, S> context) {
        Component<T, S> executed = context.getExt(Key.of(this));
        return executed != null && executed.isRollbackable(context);
    }

    @Override
    public void rollback(EngineContext<T, S> context) {
        Component<T, S> executed = context.remove(Key.of(this));
        if (executed == null || !executed.isRollbackable(context)) {
            return;
        }

        WrappedInvoker.rollback(context, executed);
    }

    @Override
    public String describe() {
        return "choose@" + condition.describe();
    }

    @Override
    public void accept(ComponentVisitor visitor) {
        visitor.visitAttributes(attributes);
        visitor.visitExtensionAttributes(this.get());
        ConditionVisitor conditionVisitor = visitor.visitCondition(condition.describe());
        if (conditionVisitor != null) {
            condition.visit(conditionVisitor);
        }
        visitor.visitSource(this);

        branches.forEach((k, v) -> {
            ComponentVisitor branchVisitor = visitor.visitBranch(k, v.getType(), v.getName(), v.describe());
            if (branchVisitor != null) {
                v.accept(branchVisitor);
            }
        });

        if (defaultBranch != null) {
            ComponentVisitor defaultVisitor = visitor.visitComponent(defaultBranch.getType(), defaultBranch.getName(), defaultBranch.describe());
            if (defaultVisitor != null) {
                defaultBranch.accept(defaultVisitor);
            }

        }

        visitor.visitEnd();
    }

    @Override
    public void doValidate() {
        AssertUtil.notNull(condition, "choose[" + getName() + "] condition must not be null");
        AssertUtil.notNull(branches, "choose[" + getName() + "] branch must not be null");
        AssertUtil.isTrue(branches.size() != 0, "choose[" + getName() + "] branch must not be null");
        branches.forEach((k, v) -> v.validate());

        if (defaultBranch != null) {
            defaultBranch.validate();
        }
    }

    @Override
    public ComponentType getType() {
        return ComponentType.CHOOSE;
    }

    @Override
    public void reset() {
        super.reset();
        this.branches.forEach((k, v) -> v.reset());
        if (defaultBranch != null) {
            defaultBranch.reset();
        }
    }
}
