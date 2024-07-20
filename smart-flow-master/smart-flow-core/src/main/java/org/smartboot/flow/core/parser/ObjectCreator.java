package org.smartboot.flow.core.parser;

/**
 * @author qinluo
 * @date 2022/11/19 14:18
 * @since 1.0.0
 */
public interface ObjectCreator {

    /**
     * Create an instance with specific type.
     *
     * @since 1.0.5
     * @param type       type
     * @param expectType expect java type.
     * @param useCache   useCache
     * @return           instance.
     */
    <T> T create(String type, Class<T> expectType, boolean useCache);
}
