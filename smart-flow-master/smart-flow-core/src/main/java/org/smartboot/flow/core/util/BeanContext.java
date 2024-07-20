package org.smartboot.flow.core.util;

import java.util.List;

/**
 * @author qinluo
 * @date 2023-08-09 11:39:16
 * @since 1.1.3
 */
public interface BeanContext {

    /**
     * Try to find a bean named name.
     *
     * @param name name.
     * @param <T>  generic type
     * @return     bean
     */
    default <T> T getBean(String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * Try to find a specified type bean named name.
     *
     * @param name name.
     * @param <T>  generic type
     * @param type specified type
     * @return     bean
     */
    default <T> T getBean(String name, Class<T> type) {
        throw new UnsupportedOperationException();
    }

    /**
     * Try to find a specified type bean list.
     *
     * @param <T>  generic type
     * @param type specified type
     * @return     bean list
     */
    default <T> List<T> getBean(Class<T> type) {
        throw new UnsupportedOperationException();
    }

    /**
     * Make cur bean ctx enabled.
     */
    default void init() {
        BeanUtils.init(this);
    }
}
