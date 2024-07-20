package org.smartboot.flow.manager.trace;

import org.smartboot.flow.core.component.Component;

/**
 * @author qinluo
 * @date 2023/2/8 1:04
 * @since 1.0.7
 */
@SuppressWarnings("rawtypes")
public class ComponentData {

    /**
     * component source.
     */
    private Component source;

    private final long start;
    private long escape;
    private Throwable ex;
    private Object ctx;
    private long rollbackStart;
    private long rollbackEnd;

    public ComponentData(Component source, Object ctx) {
        this.source = source;
        this.start = System.currentTimeMillis();
        this.ctx = ctx;

    }

    public long getRollbackStart() {
        return rollbackStart;
    }

    public void setRollbackStart(long rollbackStart) {
        this.rollbackStart = rollbackStart;
    }

    public long getRollbackEnd() {
        return rollbackEnd;
    }

    public void setRollbackEnd(long rollbackEnd) {
        this.rollbackEnd = rollbackEnd;
    }

    public Component getSource() {
        return source;
    }

    public void setSource(Component source) {
        this.source = source;
    }

    public long getStart() {
        return start;
    }

    public long getEscape() {
        return escape;
    }

    public void setEscape(long escape) {
        this.escape = escape;
    }

    public Throwable getEx() {
        return ex;
    }

    public void setEx(Throwable ex) {
        this.ex = ex;
    }

    public Object getCtx() {
        return ctx;
    }

    public void setCtx(Object ctx) {
        this.ctx = ctx;
    }
}
