package org.smartboot.flow.core;

/**
 * @author qinluo
 * @date 2022-11-17 11:45:41
 * @since 1.0.0
 */
public interface IdentifierManager {

    /**
     * generate a unique identifier.
     *
     * @param prefix prefix.
     * @return       identifier.
     */
    String allocate(String prefix);
}
