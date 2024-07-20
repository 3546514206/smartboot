package org.smartboot.flow.manager;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author qinluo
 * @date 2022-11-25 21:38:57
 * @since 1.0.0
 */
public class NamedThreadFactory implements ThreadFactory {

    private final AtomicInteger sequence = new AtomicInteger(0);
    private final String name;

    public NamedThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        if (r instanceof Thread) {
            ((Thread) r).setName(name + "-" + sequence.addAndGet(1));
            return (Thread) r;
        }

        Thread t = new Thread(r);
        t.setName(name + "-" + sequence.addAndGet(1));
        return t;
    }
}
