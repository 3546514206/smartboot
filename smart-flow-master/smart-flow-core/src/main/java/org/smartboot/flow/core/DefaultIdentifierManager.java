package org.smartboot.flow.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author qinluo
 * @date 2022-11-17 11:54:12
 * @since 1.0.0
 */
public class DefaultIdentifierManager implements IdentifierManager {

    /**
     * Generated identifiers.
     */
    private final Map<String, Integer> identifiers = new ConcurrentHashMap<>();
    private final AtomicInteger sequence = new AtomicInteger(0);

    @Override
    public String allocate(String prefix) {
        String identifier = prefix + "-" + sequence.getAndAdd(1);

        // Ensure identifier is unique.
        while (identifiers.containsKey(identifier) || identifiers.put(identifier, 1) != null) {
            identifier = prefix + "-" + sequence.getAndAdd(1);
        }

        return identifier;
    }
}
