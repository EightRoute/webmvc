package com.webmvc.helper;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.webmvc.annotation.Component;
import com.webmvc.annotation.Controller;
import com.webmvc.annotation.Repository;
import com.webmvc.annotation.Service;
import com.webmvc.util.ClassUtil;
import com.webmvc.util.StringUtil;

/**
 * 扫描包含Component注解的class
 * @author sgz
 * @date   2018年2月9日 下午2:00:44
 */
public final class ClassHelper {

	private static final Set<Class<?>> CLASS_SET;
	
	static {
		String basePackage = ConfigHelper.getAppBasePackage();
		CLASS_SET = ClassUtil.getClassSet(basePackage);
	}
	
	public static Set<Class<?>> getClassSet() {
		return CLASS_SET;
	}
	
	/**
	 * 找出包含Component注解的Type
	 * AnonymousClass 匿名类
	 * LocalClass 局部类
	 * MemberClass 内部类
	 * @return 包含所有bean的map
	 */
	public static Map<String, Class<?>> getBean(){
		Map<String, Class<?>> beanMap = new ConcurrentHashMap<String, Class<?>>();
		for (Class<?> clazz : CLASS_SET) {
			if (clazz.isAnnotationPresent(Component.class)) {
				//  如果clazz不是类似Controller,Serivce,Repository的注解		 
				if (! clazz.isAnnotation()) {
					//判断bean是否有默认的name
					String beanName = clazz.getAnnotation(Component.class).value();
					loadBean(clazz, beanMap, beanName);
					continue;
				}
				for (Class<?> cla : CLASS_SET) {
					if (cla.isAnnotationPresent(clazz.asSubclass(Annotation.class))) {
						String beanName;
						if (cla.isAnnotationPresent(Controller.class)) {
							//判断bean是否有默认的name
							beanName = cla.getAnnotation(Controller.class).value();
							loadBean(cla, beanMap, beanName);
						}
						if (cla.isAnnotationPresent(Service.class)) {
							//判断bean是否有默认的name
							beanName = cla.getAnnotation(Service.class).value();
							loadBean(cla, beanMap, beanName);
						}
						if (cla.isAnnotationPresent(Repository.class)) {
							//判断bean是否有默认的name
							beanName = cla.getAnnotation(Repository.class).value();
							loadBean(cla, beanMap, beanName);
						}
					}
				}
			}
		}
		return beanMap;
	}
	
	
	private static void loadBean(Class<?> clazz, Map<String, Class<?>> beanMap, String beanName) {
		
		if (StringUtil.isEmpty(beanName)) {
			//判断clazz是否实现了接口
			Class<?>[] fatherInters = clazz.getInterfaces();
			if (fatherInters.length > 0) {
				for (Class<?> fatherInter : fatherInters) {
					beanMap.put(fatherInter.getName(), clazz);
				}
			} else {
				beanMap.put(clazz.getName(), clazz);
			}

		} else {
			beanMap.put(beanName, clazz);
		}
	}

}
