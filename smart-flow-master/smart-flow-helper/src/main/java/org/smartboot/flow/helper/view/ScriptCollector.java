package org.smartboot.flow.helper.view;

import org.smartboot.flow.core.common.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Collect scripts in engine.
 *
 * @author qinluo
 * @date 2023-01-29 10:47:46
 * @since 1.0.5
 */
public class ScriptCollector {

    private static final ThreadLocal<ScriptCollector> HOLDER = new ThreadLocal<>();

    /**
     * script-name : script-content : script-type
     */
    private final Map<String, Pair<String, String>> scripts = new HashMap<>();

    public static void start() {
        HOLDER.set(new ScriptCollector());
    }

    public static void collect(String name, String script, String type) {
        ScriptCollector collector = HOLDER.get();
        if (collector != null) {
            collector.scripts.put(name, Pair.of(script, type));
        }
    }

    public static Map<String, Pair<String, String>> end() {
        ScriptCollector collector = HOLDER.get();
        if (collector != null) {
            HOLDER.remove();
            return collector.scripts;
        }

        return null;
    }
}
