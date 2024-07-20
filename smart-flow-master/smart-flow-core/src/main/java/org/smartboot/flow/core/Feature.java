package org.smartboot.flow.core;

/**
 * @author qinluo
 * @date 2023-04-12 20:57:35
 * @since 1.0.9
 */
public enum Feature {

    /**
     * 记录执行trace
     */
    RecordTrace,

    /**
     * 执行完毕后打印trace
     */
    PrintTrace,

    /**
     * Listener中的broken体现在invokeTree中，仅对组件生效
     */
    BrokenInListenerOnTree,

    ;

    final int mask = 1 << ordinal();
}
