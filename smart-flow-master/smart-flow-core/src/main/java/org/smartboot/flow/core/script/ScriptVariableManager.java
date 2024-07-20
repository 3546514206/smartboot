package org.smartboot.flow.core.script;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *     1、提供全局脚本变量注册
 *     2、提供基于引擎维度的脚本变量注册
 * </p>
 *
 * @author qinluo
 * @date 2023-01-17 19:41
 * @since 1.0.5
 */
public class ScriptVariableManager {

    /**
     * 全局脚本变量注册
     */
    private static final Map<String, Object> GLOBAL = new ConcurrentHashMap<>();

    /**
     * 引擎维度脚本变量注册
     */
    private static final Map<String, Map<String, Object>> ENGINE_VARIABLES = new ConcurrentHashMap<>();

    public static void register(String key, Object variable) {
        GLOBAL.put(key, variable);
    }

    public static Object remove(String key) {
        return GLOBAL.remove(key);
    }

    public synchronized static void register(String engine, String key, Object variable) {
        Map<String, Object> variables = ENGINE_VARIABLES.getOrDefault(engine, new ConcurrentHashMap<>(4));
        variables.put(key, variable);
        ENGINE_VARIABLES.put(engine, variables);
    }

    public static Object remove(String engine, String key) {
        Map<String, Object> variables = ENGINE_VARIABLES.get(engine);
        if (variables != null) {
            return variables.remove(key);
        }

        return null;
    }

    public static Map<String, Object> getRegistered(String engine) {
        Map<String, Object> allRegistered = new HashMap<>(GLOBAL);
        Map<String, Object> variables = ENGINE_VARIABLES.get(engine);
        if (variables != null) {
            allRegistered.putAll(variables);
        }
        return allRegistered;
    }
}
