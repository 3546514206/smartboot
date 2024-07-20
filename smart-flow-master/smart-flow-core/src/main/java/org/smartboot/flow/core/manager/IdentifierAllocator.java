package org.smartboot.flow.core.manager;

import org.smartboot.flow.core.IdentifierManager;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author qinluo
 * @date 2023-02-07
 * @since 1.0.0
 */
public class IdentifierAllocator implements IdentifierManager {

    private final AtomicInteger sequence = new AtomicInteger(0);

    @Override
    public String allocate(String prefix) {
        return "anonymous-" + prefix + sequence.addAndGet(1);
    }
}
