package org.smartboot.flow.spring.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注引擎更新
 *
 * @author qinluo
 * @date 2023/1/31 21:53
 * @since 1.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReloadNotify {

    /**
     * 需要通知的引擎名称列表
     * 当修饰单个引擎字段时，value值为引擎名称，不指定则属性字段名为引擎名称
     * 当修饰map时，可以指定引擎名称列表，如果不指定则默认所有引擎都会注册到map中
     *
     *
     * @return 引擎名称
     */
    String[] value() default "";
}
