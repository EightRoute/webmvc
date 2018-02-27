 package com.webmvc.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.webmvc.excepetion.WebMVCException;


/**
 * 加载properties的工具类
 * @author sgz
 * @date   2018年2月8日 下午2:57:56
 */
public class PropertiesUtil {
	
	/**
	 * 加载classpath下的properties
	 * @return Properties
	 */
	public static Properties loadClassPathProperties(String classPathConfigFile) {
		Properties properties = new Properties();
		InputStream propertiesInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(classPathConfigFile);
		try {
			properties.load(propertiesInputStream);
		} catch (IOException e) {
			throw new WebMVCException("加载" + classPathConfigFile + "时出现异常", e);
		}
		return properties;
	}
	
	/**
	 * 加载绝对路径下的properties
	 * @return Properties
	 */
	
	public static Properties loadAbsolutePathProperties(String absolutePathConfigFile) {
		Properties properties = new Properties();
		try {
			InputStream propertiesInputStream = new FileInputStream(absolutePathConfigFile);
			properties.load(propertiesInputStream);
		} catch (IOException e) {
			throw new WebMVCException("加载" + absolutePathConfigFile + "时出现异常", e);
		}
		return properties;
	}
	
	/**
	 * 根据key取properties中的value
	 * @param properties
	 * @param keyString
	 * @return valueString
	 */
	public static String getPropertiesValue(Properties properties, String keyString) {
		return properties.getProperty(keyString);
	}
	
	/**
	 * 根据key取properties中的value,如为空返回默认值defaultValue
	 * @param properties
	 * @param keyString
	 * @param defaultValue 
	 * @return valueString
	 */
	public static String getPropertiesValue(Properties properties, String keyString, String defaultValue) {
		if(null == properties.getProperty(keyString)) {
			return defaultValue;
		}
		return properties.getProperty(keyString);
	}
}
