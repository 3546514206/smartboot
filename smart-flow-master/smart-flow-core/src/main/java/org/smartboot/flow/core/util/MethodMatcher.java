package org.smartboot.flow.core.util;

import java.lang.reflect.Method;

/**
 * @author yamikaze
 * @date 2023/6/17 19:32
 * @since 1.1.0
 */
public interface MethodMatcher {

    /**
     * Match method.
     *
     * @param m method
     * @return  matched
     */
    boolean match(Method m);
}
