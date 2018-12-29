package com.jd.laf.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 扩展实现注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Extension {
    /**
     * 名称
     *
     * @return
     */
    String value() default "";

    /**
     * 排序顺序，按照优先级升序排序
     *
     * @return
     */
    int order() default Ordered.ORDER;

    /**
     * 单例
     *
     * @return
     */
    boolean singleton() default true;
}
