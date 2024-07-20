package org.smartboot.flow.core.component;


import org.smartboot.flow.core.AbstractExtensible;
import org.smartboot.flow.core.DegradeCallback;
import org.smartboot.flow.core.Describable;
import org.smartboot.flow.core.EngineContext;
import org.smartboot.flow.core.Measurable;
import org.smartboot.flow.core.Rollback;
import org.smartboot.flow.core.Validator;
import org.smartboot.flow.core.attribute.AttributeHolder;
import org.smartboot.flow.core.attribute.AttributeValueResolver;
import org.smartboot.flow.core.attribute.Attributes;
import org.smartboot.flow.core.common.ComponentType;
import org.smartboot.flow.core.visitor.ComponentVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qinluo
 * @date 2022-11-12 17:58:34
 * @since 1.0.0
 */
public abstract class Component<T, S> extends AbstractExtensible implements Rollback<T, S>, Describable, Validator, Measurable {

    /**
     * 是否可降级
     */
    private boolean degradable;

    /**
     * 是否可回滚
     */
    private boolean rollback;
    private boolean async;
    private long timeout;
    private List<String> dependsOn = new ArrayList<>(0);
    private String name;
    private boolean enabled = true;
    private DegradeCallback<T, S> degradeCallback;
    /**
     * @since 1.1.4
     */
    private transient AttributeValueResolver valueResolver = AttributeValueResolver.getInstance();

    protected final List<AttributeHolder> attributes = new ArrayList<>(8);
    private volatile boolean validateCalled = false;

    public void setValueResolver(AttributeValueResolver valueResolver) {
        this.valueResolver = valueResolver;
    }

    public DegradeCallback<T, S> getDegradeCallback() {
        return degradeCallback;
    }

    public void setDegradeCallback(DegradeCallback<T, S> degradeCallback) {
        this.degradeCallback = degradeCallback;
    }

    public boolean isDegradable() {
        return degradable;
    }

    public void setDegradable(boolean degradable) {
        this.degradable = degradable;
    }

    public void setRollback(boolean rollback) {
        this.rollback = rollback;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void addAttribute(AttributeHolder holder) {
        this.attributes.add(holder);
    }

    public void setAttributes(List<AttributeHolder> attributes) {
        this.attributes.addAll(attributes);
    }

    public List<String> getDependsOn() {
        return new ArrayList<>(dependsOn);
    }

    public void setDependsOn(List<String> dependsOn) {
        this.dependsOn = dependsOn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract int invoke(EngineContext<T, S> context) throws Throwable;

    /**
     * Check current is rollbackable.
     *
     * @param context ctx
     * @return        true/false
     */
    public boolean isRollbackable(EngineContext<T, S> context) {
        return rollback;
    }

    @Override
    public void validate() {
        if (validateCalled) {
            return;
        }

        this.validateCalled = true;
        this.doValidate();
    }

    protected void doValidate() {

    }

    /** Visit component's structure */
    public abstract void accept(ComponentVisitor visitor);

    /** Returns component's type */
    public abstract ComponentType getType();

    /**
     * 获取属性值
     *
     * @since 1.1.4
     */
    public <R> R getAttributeValue(Attributes attributes, R defaultValue) {
        for (AttributeHolder ah : this.attributes) {
            if (ah.getAttribute() == attributes) {
                return (R)valueResolver.resolve(attributes.getAccept(), ah.getValue());
            }
        }

        return defaultValue;
    }
}
