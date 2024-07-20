package org.smartboot.flow.manager;

/**
 * @author qinluo
 * @date 2023/1/30 22:43
 * @since 1.0.7
 */
public class ManagerConfiguration {

    /**
     * 异常信息上报最大堆栈神对，默认10
     */
    public static int reportMaxStackDepth = 10;

    /**
     * 采集Request或者result配置
     */
    public static boolean traceRequest = false;
    public static boolean traceResult = false;
}
