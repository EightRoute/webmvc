package com.webmvc.helper;

import com.webmvc.excepetion.WebMVCException;
import com.webmvc.util.ReflectionUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sgz
 * 2018/2/10 22:21
 */
public final class BeanHelper {

    private static final Map<String, Object> BEAN_MAP = new ConcurrentHashMap<>();

    static {
        Map<String, Class<?>> beanClassMap =ClassHelper.getBean();
        for (Map.Entry<String, Class<?>> entry : beanClassMap.entrySet()) {
            Object obj = ReflectionUtil.newInstance(entry.getValue());
            BEAN_MAP.put(entry.getKey(), obj);
        }
    }
    /**
     * 获取包含bean的Map
     * @return  包含bean的Map
     */
    public static Map<String, Object> getBeanMap() {
        return  BEAN_MAP;
    }

    /**
     * 根据名字和class获取对象
     * @param name bean的名字
     * @return 从bean容器中获取的对象
     */
    public static <T> T getBean(String name) {

        if (! BEAN_MAP.containsKey(name) ) {
            throw new WebMVCException("没有找到所需要的bean" );
        }
        try {
            return (T) BEAN_MAP.get(name);
        } catch (Exception e) {
            throw new WebMVCException("实例化bean时出错",e);
        }
    }
}
