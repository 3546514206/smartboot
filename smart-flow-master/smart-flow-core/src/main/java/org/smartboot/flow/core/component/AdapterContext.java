package org.smartboot.flow.core.component;

import org.smartboot.flow.core.EngineContext;

/**
 * @author qinluo
 * @date 2022/12/11 20:09
 * @since 1.0.0
 */
public class AdapterContext<T, S> extends EngineContext<T, S> {

    @Override
    public boolean getRollback() {
        return super.getRollback();
    }

    @Override
    public void setRollback(boolean rollback) {
        this.parent.setRollback(rollback);
        super.setRollback(rollback);
    }

    @Override
    public Throwable getFatal() {
        return super.getFatal();
    }

    @Override
    public void setFatal(Throwable fatal) {
        this.parent.setFatal(fatal);
        super.setFatal(fatal);
    }

    @Override
    public void broken(boolean broken) {
        super.brokenAll(broken);
    }

    public void setParent(EngineContext ctx) {
        this.parent = ctx;
    }
}
