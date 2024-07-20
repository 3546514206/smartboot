package org.smartboot.plugin.executable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yamikaze
 * @date 2023/6/17 22:04
 * @since 1.1.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {

    /**
     * Parameter's name.
     *
     * @return name.
     */
    String value();
}
