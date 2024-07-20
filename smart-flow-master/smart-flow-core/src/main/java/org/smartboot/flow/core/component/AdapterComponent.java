package org.smartboot.flow.core.component;

import org.smartboot.flow.core.Adapter;
import org.smartboot.flow.core.EngineContext;
import org.smartboot.flow.core.Key;
import org.smartboot.flow.core.common.ComponentType;
import org.smartboot.flow.core.common.Pair;
import org.smartboot.flow.core.invoker.WrappedInvoker;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.visitor.ComponentVisitor;

/**
 * 适配器组件
 *
 * @author huqiang
 * @since 2022/12/7 19:01
 */
public class AdapterComponent<T, S, P, Q> extends Component<T, S> {

    /**
     * 业务适配器接口定义
     */
    private Adapter<T, S, P, Q> adapter;

    /**
     * 待执行的适配流程组件
     */
    private Component<P, Q> component;

    public void setAdapter(Adapter<T, S, P, Q> adapter) {
        this.adapter = adapter;
    }

    public void setComponent(Component<P, Q> component) {
        this.component = component;
    }

    @Override
    public String describe() {
        return "adapter@" + adapter.describe();
    }

    @Override
    public void rollback(EngineContext<T, S> context) {
        AdapterContext<P, Q> newContext = context.remove(Key.of(this));
        if (newContext == null) {
            return;
        }
        newContext.setExecuting(EngineContext.ROLLBACK);
        WrappedInvoker.rollback(newContext, component);
    }

    @Override
    public boolean isRollbackable(EngineContext<T, S> context) {
        AdapterContext<P, Q> newContext = context.getExt(Key.of(this));
        return newContext != null;
    }

    @Override
    public void doValidate() {
        AssertUtil.notNull(adapter, "adapter[" + getName() + "] adapter must not be null");
        AssertUtil.notNull(component, "component[" + getName() + "] component must not be null");

        component.doValidate();
    }

    @Override
    public int invoke(EngineContext<T, S> context) throws Throwable {
        // Convert.
        Pair<P, Q> pair = adapter.before(context);
        AssertUtil.notNull(pair, "adapter[" + getName() + "] result must not be null");

        // Adapter ctx 本质只是进行参数的转换，并不是像子流程那样新起一个ctx，所以对一些操作类的方法，相关属性需要回设到父流程去
        AdapterContext<P, Q> newContext = new AdapterContext<>();
        newContext.setReq(pair.getLeft());
        newContext.setResult(pair.getRight());
        // copy parent
        context.copy(newContext);
        newContext.setParent(context);

        // Store converted objects.
        context.putExt(Key.of(this), newContext);

        int invoke = WrappedInvoker.invoke(newContext, component);
        // Apply result to parent context.
        adapter.after(context, newContext);
        return invoke;
    }

    @Override
    public void accept(ComponentVisitor visitor) {
        visitor.visitAttributes(attributes);
        visitor.visitExtensionAttributes(this.get());
        // Ignored executable visitor.
        visitor.visitExecutable(this.adapter.describe());
        visitor.visitSource(this);

        ComponentVisitor componentVisitor = visitor.visitComponent(component.getType(), component.getName(), component.describe());
        if (componentVisitor != null) {
            component.accept(componentVisitor);
        }
        visitor.visitEnd();
    }

    @Override
    public ComponentType getType() {
        return ComponentType.ADAPTER;
    }

    @Override
    public void reset() {
        super.reset();
        this.component.reset();
    }
}
