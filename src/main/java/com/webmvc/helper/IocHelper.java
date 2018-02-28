package com.webmvc.helper;

import com.webmvc.annotation.Autowire;
import com.webmvc.excepetion.WebMVCException;
import com.webmvc.util.ArrayUtil;
import com.webmvc.util.CollectionUtil;
import com.webmvc.util.ReflectionUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by sgz
 * 2018/2/10 22:49
 */
public final class IocHelper {

    static {
        System.out.println("----开始注入----");
        Map<String, Object> beanMap = BeanHelper.getBeanMap();
        if (CollectionUtil.isNotEmpty(beanMap)) {
            for (Map.Entry<String, Object> beanEntry : beanMap.entrySet()) {
                Object beanObject = beanEntry.getValue();
                Field[] beanFileds = beanObject.getClass().getDeclaredFields();
                if (ArrayUtil.isNotEmpty(beanFileds)) {
                    for (Field beanFiled : beanFileds) {
                       /*找出含有Autowire注解的域，排除HttpServletResponse和HttpServletRequest*/
                        if (beanFiled.isAnnotationPresent(Autowire.class)
                                && beanFiled.getType() != HttpServletResponse.class
                                && beanFiled.getType() != HttpServletRequest.class) {
                            String filedName = beanFiled.getName();
                            Class<?> filedClass = beanFiled.getType();
                            Object valueObject;
                            if (beanMap.get(filedName) != null) {
                                valueObject = beanMap.get(filedName);
                            } else {
                                valueObject = beanMap.get(filedClass.getName());
                            }
                            try {
                                ReflectionUtil.setFiled(beanObject, beanFiled, valueObject);
                            } catch (Exception e) {
                                throw new WebMVCException("给" + beanObject.getClass().getName() + "中的" + beanFiled.getName() + "注入值时出错", e);
                            }
                        }
                    }
                }
            }
        }
    }
}
