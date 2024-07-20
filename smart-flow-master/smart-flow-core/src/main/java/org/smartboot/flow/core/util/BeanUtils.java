package org.smartboot.flow.core.util;

import java.util.List;

/**
 * @author qinluo
 * @date 2023-08-09 11:36:35
 * @since 1.1.3
 */
public class BeanUtils {

    private static BeanContext instance = null;

    /**
     * Init bean context.
     *
     * @param ctx bean context
     */
    static void init(BeanContext ctx) {
        instance = ctx;
    }

    /* Static delegate methods */

    public static <T> T getBean(String name) {
        return instance.getBean(name);
    }

    public static <T> T getBean(String name, Class<T> type) {
        return instance.getBean(name, type);
    }

    public static <T> T getBean(Class<T> type) {
        List<T> beans = instance.getBean(type);
        return beans != null && beans.size() > 0 ? beans.get(0) : null;
    }
}
