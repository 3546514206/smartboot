package org.smartboot.flow.manager.trace;

import org.smartboot.flow.core.EngineContext;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author qinluo
 * @date 2023/2/8 1:09
 * @since 1.0.0
 */
public class TraceSampleStrategy {

    private double radio;

    public double getRadio() {
        return radio;
    }

    public void setRadio(double radio) {
        this.radio = radio;
    }

    public <T, S> boolean sampled(EngineContext<T, S> ctx) {
        return ThreadLocalRandom.current().nextDouble(1) < radio;
    }

    public <T, S> boolean sampled2(EngineContext<T, S> ctx) {
        return true;
    }
}
