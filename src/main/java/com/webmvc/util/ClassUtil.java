package com.webmvc.util;

import java.util.HashSet;
import java.util.Set;

import com.webmvc.excepetion.WebMVCException;

/**
 * @author sgz
 * @date   2018年2月8日 下午3:34:49
 */
public final class ClassUtil {

	/**
	 * 获取当前线程的类加载器
	 */
	public static ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
	
	/**
	 * 根据类名加载类
	 * @param className 类名
	 * @param isInitialized 是否初始化
	 * @return Class对象
	 */
	public static Class<?> loadClass(String className, boolean isInitialized) {
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className, isInitialized, getClassLoader());
		} catch (ClassNotFoundException e) {
			throw new WebMVCException("加载类时出错", e);
		}
		return clazz;
	}
	
	
	/**
	 * 根据包名加载包下所有的类
	 * @param packageName
	 * @return 包含所有类的集合
	 */
	public static Set<Class<?>> getClassSet(String packageName) {
		Set<Class<?>> classSet = new HashSet<Class<?>>();
		return null;
	}
}
