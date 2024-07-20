package org.smartboot.flow.spring.extension;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启引擎reload通知
 *
 * @author qinluo
 * @date 2023/1/31 22:16
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SmartFlowRegistrar.class)
public @interface EasySmartFlow {
}
