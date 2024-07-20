package org.smartboot.flow.core.executable;


import org.smartboot.flow.core.EngineContext;

/**
 * 调用层，与业务相关
 *
 * @author qinluo
 * @date 2022-11-12 21:29:01
 * @since 1.0.0
 */
public abstract class AbstractExecutable<T, S> implements Executable<T, S> {

    @Override
    public void execute(EngineContext<T, S> context) {
        this.execute(context.getReq(), context.getResult());
    }

    public void execute(T t, S s) {

    }

    @Override
    public void rollback(EngineContext<T, S> context) {
        this.rollback(context.getReq(), context.getResult());
    }

    public void rollback(T t, S s) {

    }
}
