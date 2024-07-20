package org.smartboot.flow.core.executable;


import org.smartboot.flow.core.DegradeCallback;
import org.smartboot.flow.core.EngineContext;
import org.smartboot.flow.core.Key;
import org.smartboot.flow.core.Rollback;
import org.smartboot.flow.core.common.ComponentType;
import org.smartboot.flow.core.component.Component;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.visitor.ComponentVisitor;
import org.smartboot.flow.core.visitor.ExecutableVisitor;

/**
 * @author qinluo
 * @date 2022-11-12 21:58:34
 * @since 1.0.0
 */
public class ExecutableAdapter<T, S> extends Component<T,S> implements Rollback<T, S> {

    private Executable<T, S> executable;

    public ExecutableAdapter() {

    }

    public ExecutableAdapter(Executable<T, S> executable) {
        this.executable = executable;
    }

    public void setExecutable(Executable<T, S> executable) {
        this.executable = executable;
    }

    @Override
    public int invoke(EngineContext<T, S> context) {
        try {
            // Record executed.
            context.putExt(Key.of(this), true);
            executable.execute(context);
        } catch (Throwable e) {
            // 非降级，直接throw
            if (!isDegradable()) {
                throw e;
            }

            // 触发降级回调
            if (getDegradeCallback() != null) {
                getDegradeCallback().doWithDegrade(context, e);
            } else if (executable instanceof DegradeCallback) {
                ((DegradeCallback<T, S>) executable).doWithDegrade(context, e);
            }

        }

        return 1;
    }

    @Override
    public boolean isRollbackable(EngineContext<T, S> context) {
        // Attribute 'rollback' configured and component has executed.
        return super.isRollbackable(context) && context.getExt(Key.<Boolean>of(this)) != null;
    }

    @Override
    public void rollback(EngineContext<T, S> context) {
        context.remove(Key.of(this));
        executable.rollback(context);
    }

    @Override
    public String describe() {
        return executable.describe();
    }

    @Override
    public void accept(ComponentVisitor visitor) {
        visitor.visitAttributes(attributes);
        visitor.visitExtensionAttributes(this.get());
        visitor.visitSource(this);
        ExecutableVisitor executableVisitor = visitor.visitExecutable(executable.describe());
        if (executableVisitor != null) {
            executable.visit(executableVisitor);
        }
        visitor.visitEnd();
    }

    @Override
    public void doValidate() {
        AssertUtil.notNull(executable, "Executable[" + getName() + "]executable must not be null");
    }

    @Override
    public ComponentType getType() {
        return ComponentType.BASIC;
    }
}
