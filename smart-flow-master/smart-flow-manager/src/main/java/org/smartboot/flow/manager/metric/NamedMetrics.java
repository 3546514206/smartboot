package org.smartboot.flow.manager.metric;

/**
 * @author qinluo
 * @date 2022/11/23 21:45
 * @since 1.0.0
 */
public interface NamedMetrics {

    String EXECUTE = "execute";
    String FAIL = "fail";
    String ROLLBACK = "rollback";
    String MAX_ESCAPE = "maxEscape";
    String TOTAL_ESCAPE = "totalEscape";
    String ROLLBACK_MAX_ESCAPE = "rollbackMaxEscape";
    String ROLLBACK_TOTAL_ESCAPE = "rollbackTotalEscape";
}
