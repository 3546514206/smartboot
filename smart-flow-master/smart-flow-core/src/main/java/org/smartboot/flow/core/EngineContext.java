package org.smartboot.flow.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.exception.ExceptionHandler;
import org.smartboot.flow.core.invoker.Invoker;
import org.smartboot.flow.core.trace.Tracer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @author qinluo
 * @date 2022-11-12 21:00:58
 * @since 1.0.0
 */
public class EngineContext<T, S>{

    public static final int DISABLED = -1;
    public static final int EXECUTING = 1;
    public static final int ROLLBACK = 2;

    public static final Logger LOGGER = LoggerFactory.getLogger(EngineContext.class);
    protected T req;
    protected S result;
    protected ExecutorService executor;
    protected Map<Key<?>, Value> extensions = new ConcurrentHashMap<>();
    protected Tracer tracer = new Tracer();
    protected boolean broken;
    protected Throwable fatal;
    protected ExceptionHandler handler;
    protected boolean rollback;
    protected Map<String, AsyncCallResult> asyncInvokes = new ConcurrentHashMap<>();
    protected String engineName;
    protected ExecutionListener listener;
    protected Invoker invoker;

    /**
     * Executing time in mills.
     *
     * @since 1.1.0
     */
    protected long executedAt;

    /**
     * Execute completed time in mills.
     *
     * @since 1.1.0
     */
    protected long completedAt;

    /**
     * @since 1.0.9
     */
    protected SmartFlowConfiguration cfg = SmartFlowConfiguration.newCfg();
    /**
     * 执行状态
     */
    protected int executing;
    protected EngineContext parent;

    /**
     * Returns escaped mills in current invoke.
     *
     * @since 1.1.0
     * @return invoke escape.
     */
    public long escaped() {
        return completedAt >= executedAt ? completedAt - executedAt : -1;
    }

    public <P, Q> void copy(EngineContext<P, Q> dest) {
        // Reuse extensions.
        dest.extensions = this.extensions;
        dest.invoker = this.invoker;
        dest.tracer = this.tracer;
        dest.engineName = this.engineName;
        dest.executing = this.executing;
        dest.listener = this.listener;
        dest.handler = this.handler;
        dest.executor = this.executor;
        dest.fatal = this.fatal;
        dest.broken = this.broken;
        dest.asyncInvokes = this.asyncInvokes;
        dest.cfg = this.cfg;
    }

    /**
     * New context for subprocess.
     *
     */
    public EngineContext<T, S> newContext() {
        EngineContext<T, S> newContext = new EngineContext<>();
        newContext.setReq(req);
        newContext.setResult(result);
        // Reuse extensions.
        newContext.extensions = this.extensions;
        newContext.invoker = this.invoker;
        newContext.tracer = this.tracer;
        newContext.engineName = this.engineName;
        newContext.executing = this.executing;
        newContext.listener = this.listener;
        newContext.handler = this.handler;
        newContext.parent = this;
        newContext.executor = this.executor;
        newContext.cfg = this.cfg;
        return newContext;
    }

    public SmartFlowConfiguration cfg() {
        return cfg;
    }

    /**
     * Returns current invoked trace.
     *
     * @since 1.0
     */
    public String getTrace() {
        return tracer.getTrace();
    }

    public Invoker getInvoker() {
        return invoker;
    }

    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }

    public boolean getRollback() {
        return rollback;
    }

    public void setRollback(boolean rollback) {
        this.rollback = rollback;
    }

    public Throwable getFatal() {
        return fatal;
    }

    public void setFatal(Throwable fatal) {
        this.fatal = fatal;
    }

    public boolean isBroken() {
        return broken;
    }

    /**
     * Break current pipeline.
     */
    public void broken(boolean broken) {
        this.broken = broken;
    }

    /**
     * Broken full pipeline
     */
    public void brokenAll(boolean broken) {
        this.broken = broken;
        if (this.parent != null) {
            this.parent.brokenAll(broken);
        }
    }

    public T getReq() {
        return req;
    }

    public S getResult() {
        return result;
    }

    public Tracer getTracer() {
        return tracer;
    }

    public void setReq(T req) {
        this.req = req;
    }

    public void setResult(S result) {
        this.result = result;
    }

    public ExceptionHandler getHandler() {
        return handler;
    }

    public void setHandler(ExceptionHandler handler) {
        this.handler = handler;
    }

    public int getExecuting() {
        return executing;
    }

    public void setExecuting(int executing) {
        this.executing = executing;
    }

    public void setListener(ExecutionListener listener) {
        this.listener = listener;
    }

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public void addAsyncInvoke(AsyncCallResult<T, S> result) {
        this.asyncInvokes.put(result.getName(), result);
    }

    public AsyncCallResult<T, S> getAsyncCall(String name) {
        return this.asyncInvokes.get(name);
    }

    public void ensureFinished() {
        this.asyncInvokes.forEach((k, v) -> v.checkAndWait(this));
    }

    public void enter(Object obj) {
        if (!cfg.isConfigured(Feature.RecordTrace)) {
            return;
        }

        String message = this.executing == ROLLBACK ? "rollback " : "";
        if (obj instanceof Describable) {
            message += (((Describable) obj).describe());
        } else if (obj instanceof String) {
            message += (obj);
        }

        this.tracer.enter(message);
    }

    public void beforeExecute(Object obj) {
        if (executing == EXECUTING)  {
            listener.beforeExecute(this, obj);
        } else if (executing == ROLLBACK) {
            listener.beforeRollback(this, obj);
        }
    }

    public void exit(Object obj) {
        exit(obj, null);
    }

    public void exit(Object obj, Throwable ex) {
        if (!cfg.isConfigured(Feature.RecordTrace)) {
            return;
        }
        this.tracer.exit();
    }

    public void afterExecute(Object obj, Throwable ex) {
        if (executing == EXECUTING)  {
            listener.afterExecute(this, obj, ex);
        } else if (executing == ROLLBACK) {
            listener.afterRollback(this, obj);
        }
    }

    /**
     * Returns all extension in copy.
     *
     * @since 1.1.0
     * @return all extension
     */
    public Map<Key<?>, Object> getAllExt() {
        Map<Key<?>, Object> copied = new HashMap<>(extensions.size());
        extensions.forEach((k, v) -> {
            if (v == Value.NULL) {
                return;
            }

            copied.put(k, v.val);
        });

        return copied;
    }

    @SuppressWarnings("unchecked")
    public <P> P getExt(Key<P> key) {
        Value value = extensions.get(key);
        if (value == null || value == Value.NULL) {
            return null;
        }

        return (P) value.get();
    }

    public <P> void putExt(Key<P> key, P ext) {
        Value value;
        if (ext == null) {
            value = Value.NULL;
        } else {
            value = new Value(ext);
        }

        extensions.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <P> P remove(Key<P> key) {
        Value removed = extensions.remove(key);
        if (removed == null || removed == Value.NULL) {
            return null;
        }

        return (P) removed.get();
    }

    public void clear() {
        this.tracer.reset();
        this.asyncInvokes.clear();
        this.extensions.clear();
        this.broken = false;
        this.rollback = false;
        this.fatal = null;
        this.listener = null;
        this.engineName = null;
        this.executing = DISABLED;
        this.executor = null;
        this.parent = null;
        this.cfg = null;
        this.completedAt = 0;
        this.executedAt = 0;
    }

    /**
     * Apply subprocess fields to parent ctx.
     */
    public void apply() {

    }

    private static class Value implements Serializable {
        private static final long serialVersionUID = 2429532033706308385L;
        private static final Value NULL = new Value(null);
        private final Object val;

        Value(Object val) {
            this.val = val;
        }

        public Object get() {
            return val;
        }
    }
}
