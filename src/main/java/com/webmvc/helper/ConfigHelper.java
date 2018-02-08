package com.webmvc.helper;

import java.util.Properties;

import com.webmvc.ConfigConstant;
import com.webmvc.util.PropertiesUtil;

/**
 * 获取主配置文件的参数
 * @author sgz
 * @date   2018年2月8日 下午3:30:12
 */
public final class ConfigHelper {
	private static final Properties CONFIG_PROPERTIES = PropertiesUtil.loadClassPathProperties(ConfigConstant.CONFIG_FILE);

	/** 
	 * 获取JdbcDriver
	 * @return JdbcDriver
	 */
	public static String getJdbcDriver() {
		return PropertiesUtil.getPropertiesValue(CONFIG_PROPERTIES, ConfigConstant.JDBC_DRIVER);
	}
	
	public static String getJdbcUrl() {
		return PropertiesUtil.getPropertiesValue(CONFIG_PROPERTIES, ConfigConstant.JDBC_URL);
	}
	
	public static String getJdbcUsername() {
		return PropertiesUtil.getPropertiesValue(CONFIG_PROPERTIES, ConfigConstant.JDBC_USERNAME);
	}
	
	public static String getJdbcUserPassword() {
		return PropertiesUtil.getPropertiesValue(CONFIG_PROPERTIES, ConfigConstant.JDBC_PASSWORD);
	}
	
	public static String getAppBasePackage() {
		return PropertiesUtil.getPropertiesValue(CONFIG_PROPERTIES, ConfigConstant.APP_BASE_PACKAGE);
	}
	
	public static String getAppJspPath() {
		return PropertiesUtil.getPropertiesValue(CONFIG_PROPERTIES, ConfigConstant.APP_JSP_PATH, "/WEB-INF/view/");
	}
	
	public static String getAppAssetPath() {
		return PropertiesUtil.getPropertiesValue(CONFIG_PROPERTIES, ConfigConstant.APP_ASSET_PATH, "/asset/");
	}
	
	
}
