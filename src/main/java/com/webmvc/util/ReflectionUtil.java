package com.webmvc.util;

import com.webmvc.excepetion.WebMVCException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射工具类
 * Created by sgz
 * 2018/2/10 22:02
 */
public final class ReflectionUtil {

    /**
     * 创建实例
     * @param clazz y要被实例化的class
     * @return 实例化对象
     */
    public static Object newInstance(Class<?> clazz) {
        Object instance;
        try {
            instance = clazz.newInstance();
        } catch (Exception e) {
            throw new WebMVCException("实例化对象时出错", e);
        }
        return instance;
    }


    /**
     * 调用方法
     * @param obj 方法所在的对象
     * @param method 要被执行的方法
     * @param  args 方法的参数
     * @return 方法执行的结果
     */
    public static Object invokeMethod(Object obj, Method method, Object... args) {
        Object result;

        //取消访问检查
        method.setAccessible(true);
        try {
            result = method.invoke(obj, args);
        } catch (Exception e) {
            throw new WebMVCException("调用方法时出错,方法为:" + method, e);
        }
        return result;
    }

    /**
     * 设置成员变量的值
     * @param obj 方法所在的对象
     * @param field 要被设值的域
     * @param value 要设的值
     */
    public static void setFiled(Object obj, Field field, Object value) {
        field.setAccessible(true);
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new WebMVCException("修改成员变量的值时出错", e);
        }

    }

}
