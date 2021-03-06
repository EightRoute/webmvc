package com.webmvc.annotation;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/**
 * Created by A550V
 * 2018/3/1 21:14
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface After {
    String value();
    String argNames() default "";
}
