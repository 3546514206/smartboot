package org.smartboot.smart.flow.admin.g6;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author qinluo
 * @date 2023/2/10 20:52
 * @since 1.0.0
 */
public class EngineAnalyzer {

    public void analyze(Node node) {

    }

    public void analyze(Combo combo) {

    }

    public void analyze(Edge edge) {

    }

    protected double calculate(double v1, double v2) {
        return BigDecimal.valueOf(v1)
                .divide(BigDecimal.valueOf(v2), 2, RoundingMode.HALF_UP)
                .stripTrailingZeros().doubleValue();
    }
}
