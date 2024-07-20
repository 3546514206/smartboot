package org.smartboot.flow.core;

/**
 * @author qinluo
 * @date 2023-07-27 14:55:42
 * @since 1.1.3
 */
@SuppressWarnings("rawtypes")
public class EngineContextHelper {

    private static final ThreadLocal<EngineContext> HOLDER = new ThreadLocal<>();

    public static void set(EngineContext ctx) {
        HOLDER.set(ctx);
    }

    public static EngineContext get() {
        return HOLDER.get();
    }

    public static void remove() {
        HOLDER.remove();
    }

    public static void broken() {
        EngineContext ctx = get();
        if (ctx != null) {
            ctx.broken(true);
        }
    }

    public static void brokenAll() {
        EngineContext ctx = get();
        if (ctx != null) {
            ctx.brokenAll(true);
        }
    }

}
