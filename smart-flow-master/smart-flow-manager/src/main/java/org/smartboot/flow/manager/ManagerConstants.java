package org.smartboot.flow.manager;

/**
 * @author qinluo
 * @date 2023/1/30 22:43
 * @since 1.0.7
 */
public interface ManagerConstants {

    /**
     * 快照查询，决定是否需要上报引擎结构
     */
    String SNAPSHOT_QUERY = "/api/report/query";
    String SNAPSHOT_QUERY_PARAM = "engineMd5";

    /**
     * 上报统计数据
     */
    String REPORT_METRICS = "/api/report/metrics";

    /**
     * 上报执行链路数据
     */
    String REPORT_TRACE = "/api/report/trace";

    /**
     * 拉取待执行的commands.
     */
    String MANAGER_COMMAND = "/api/manager/commands";

    /**
     * Http success code.
     */
    int SUCCESS = 200;
}
