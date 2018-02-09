package com.webmvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.webmvc.enums.RequestMethod;

/**
 * @author sgz
 * @date   2018年2月9日 下午1:41:57
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {

	String name() default "";
	
	String[] value() default {};
	
	RequestMethod[] method() default {};
	
	String[] params() default {};
	
	String[] headers() default {};
	
	String[] consumes() default {};
	
	String[] produces() default {};
}
