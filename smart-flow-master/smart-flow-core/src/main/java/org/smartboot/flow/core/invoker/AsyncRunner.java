package org.smartboot.flow.core.invoker;

import org.smartboot.flow.core.EngineContext;
import org.smartboot.flow.core.EngineContextHelper;
import org.smartboot.flow.core.trace.Node;
import org.smartboot.flow.core.trace.Tracer;

import java.util.concurrent.Callable;

/**
 * @author qinluo
 * @date 2022-12-07 21:57:39
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public class AsyncRunner implements Callable<Integer> {

    private final Callable<Integer> task;
    private final Node root;
    private final long threadId;
    private final EngineContext ctx;

    public AsyncRunner(Callable<Integer> task) {
        this.task = task;
        this.root = Tracer.get();
        this.ctx = EngineContextHelper.get();
        this.threadId = Thread.currentThread().getId();
    }

    @Override
    public Integer call() throws Exception {
        this.doBeforeRun();

        try {
            return task.call();
        } finally {
            this.doAfterRun();
        }
    }

    protected void doBeforeRun() {
        long tid = Thread.currentThread().getId();
        if (tid != threadId) {
            Tracer.setNode(root);
        }
        EngineContextHelper.set(ctx);
    }

    protected void doAfterRun() {
        long tid = Thread.currentThread().getId();
        if (tid != threadId) {
            Tracer.remove();
        }
        EngineContextHelper.remove();
    }
}
