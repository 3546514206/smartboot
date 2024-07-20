package org.smartboot.flow.core.attribute;


import org.smartboot.flow.core.DegradeCallback;
import org.smartboot.flow.core.component.Component;

import java.util.List;


/**
 * @author qinluo
 * @date 2022-11-12 18:46:30
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public enum Attributes {

    /**
     * name
     */
    NAME("name", "组件名称", String.class) {
        @Override
        public <T, S> void apply(Component<T, S> component, Object value) {
            component.setName((String) value);
        }
    },

    ROLLBACK("rollback", "是否开启回滚", Boolean.class) {
        @Override
        public <T, S> void apply(Component<T, S> component, Object value) {
            component.setRollback((Boolean) value);
        }
    },

    DEGRADABLE("degradable", "是否开启降级", Boolean.class) {
        @Override
        public <T, S> void apply(Component<T, S> component, Object value) {
            component.setDegradable((Boolean) value);
        }
    },

    ASYNC("async", "是否开启异步", Boolean.class) {
        @Override
        public <T, S> void apply(Component<T, S> component, Object value) {
            component.setAsync((Boolean) value);
        }
    },

    TIMEOUT("timeout", "异步超时时间", Long.class) {
        @Override
        public <T, S> void apply(Component<T, S> component, Object value) {
            component.setTimeout(((Number) value).longValue());
        }
    },

    DEPENDS_ON("dependsOn", "依赖的异步组件", List.class) {
        @Override
        public <T, S> void apply(Component<T, S> component, Object value) {
            //noinspection unchecked
            component.setDependsOn((List<String>)value);
        }
    },

    /**
     * 控制组件开启或者关闭
     *
     * @since 1.0.0
     */
    ENABLED("enabled", "启用开关", Boolean.class) {
        @Override
        public <T, S> void apply(Component<T, S> component, Object value) {
            component.setEnabled((Boolean) value);
        }
    },

    /**
     * 降级回调
     *
     * @since 1.0.2
     */
    DEGRADE_CALLBACK("degrade-callback", "降级回调", DegradeCallback.class) {
        @Override
        public <T, S> void apply(Component<T, S> component, Object value) {
            component.setDegradeCallback((DegradeCallback<T, S>) value);
        }
    },

    /**
     * 是否引用子流程
     *
     * @since 1.0.9
     */
    REFERENCED_PIPELINE("referenced-pipeline", "引用子流程", Boolean.class, false),

    /**
     * 是否等待所有异步组件完成
     *
     * @since 1.1.4
     */
    DEPENDS_ALL("dependsAll", "等待前置的所有异步组件完成", Boolean.class),

    ;

    private final String name;

    /**
     * Attribute accepted type.
     */
    protected final Class<?> accept;

    private final String description;

    /**
     * Determine attributes is visible.
     *
     * @since 1.0.9
     */
    private final boolean visible;

    Attributes(String name, String description, Class<?> accept) {
        this.name = name;
        this.accept = accept;
        this.description = description;
        this.visible = true;
    }

    Attributes(String name, String description, Class<?> accept, boolean visible) {
        this.name = name;
        this.accept = accept;
        this.description = description;
        this.visible = visible;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Class<?> getAccept() {
        return accept;
    }

    public boolean accept(Object value) {
        return accept.isInstance(value);
    }

    public <T, S> void apply(Component<T, S> component, Object value) {

    }

    public boolean isVisible() {
        return visible;
    }

    public static Attributes byName(String name) {
        for (Attributes v : values()) {
            if (v.name.equals(name) && v.visible) {
                return v;
            }
        }

        return null;
    }
}
