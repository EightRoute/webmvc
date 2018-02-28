package com.webmvc.helper;

import java.util.HashSet;
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
			System.out.println("开始扫描bean");
			if (clazz.isAnnotationPresent(Component.class)) {
				String beanName = clazz.getAnnotation(Component.class).value();
				loadBean(clazz, beanMap, beanName);
			}
			if (clazz.isAnnotationPresent(Controller.class)) {
				String beanName = clazz.getAnnotation(Controller.class).value();
				loadBean(clazz, beanMap, beanName);
			}
			if (clazz.isAnnotationPresent(Service.class)) {
				String beanName = clazz.getAnnotation(Service.class).value();
				loadBean(clazz, beanMap, beanName);
			}
			if (clazz.isAnnotationPresent(Repository.class)) {
				String beanName = clazz.getAnnotation(Repository.class).value();
				loadBean(clazz, beanMap, beanName);
			}
		}
		return beanMap;
	}
	
	
	private static void loadBean(Class<?> clazz, Map<String, Class<?>> beanMap, String beanName) {
		//如果没有默认的名字
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

	/**
	 * @return 所有包含controller注解的类
	 */
	public static Set<Class<?>> getControllerClassSet() {
		Set<Class<?>> classSet = new HashSet<>();
		for (Class<?> clazz : CLASS_SET) {
			if (clazz.isAnnotationPresent(Controller.class)) {
				classSet.add(clazz);
			}
		}
		return classSet;
	}

}
