package org.smartboot.flow.manager.metric;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author qinluo
 * @date 2022/11/23 21:41
 * @since 1.0.0
 */
public class MaxCounter extends Counter {

    private final AtomicLong counter = new AtomicLong();

    @Override
    public void increment(long value) {
        while (counter.get() < value) {
            counter.compareAndSet(counter.get(), value);
        }

    }

    @Override
    public long getValue() {
        return counter.get();
    }

    @Override
    public void reset() {
        super.reset();
        this.counter.set(0);
    }

    @Override
    public String toString() {
        return counter.toString();
    }
}
