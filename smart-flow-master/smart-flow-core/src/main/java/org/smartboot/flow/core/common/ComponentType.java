package org.smartboot.flow.core.common;

/**
 * 组件类型
 *
 * @author yamikaze
 * @date 2022/11/14
 */
public enum ComponentType {

    /**
     * 基本组件
     */
    BASIC,

    /**
     * IF分支选择组件
     */
    IF,

    /**
     * CHOOSE分支选择组件
     */
    CHOOSE,

    /**
     * 子流程组件
     */
    SUBPROCESS,

    /**
     * 适配器组件
     */
    ADAPTER,

}
