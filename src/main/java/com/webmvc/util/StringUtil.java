package com.webmvc.util;

/** 
 * @author sgz
 * @date   2018年2月9日 上午10:50:04
 */
public class StringUtil {
	
	/**
	 * 判断字符串是否不为空
	 */
	public static Boolean isNotEmpty(String string) {
		return string != null && !string.equals("");
	}
	
	/**
	 * 判断字符串是否为空
	 */
	public static Boolean isEmpty(String string) {
		return string == null || "".equals(string);
	}

}
