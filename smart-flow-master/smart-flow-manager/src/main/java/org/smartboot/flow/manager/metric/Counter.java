package org.smartboot.flow.manager.metric;

import java.util.concurrent.atomic.LongAdder;

/**
 * Count some metrics.
 *
 * @author qinluo
 * @date 2022/11/23 21:35
 * @since 1.0.0
 */
public class Counter {

    protected final LongAdder sum = new LongAdder();

    public void increment(long value) {
        sum.add(value);
    }

    public long getValue() {
        return sum.sum();
    }

    public void reset() {
        this.sum.reset();
    }

    @Override
    public String toString() {
        return String.valueOf(sum.sum());
    }
}
