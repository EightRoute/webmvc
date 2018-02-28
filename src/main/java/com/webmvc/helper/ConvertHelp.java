package com.webmvc.helper;



/**
 * Created by A550V
 * 2018/2/28 22:26
 */
public class ConvertHelp {

    /**
     * 将未知类型转化为基本类型
     * @param o 要转化的对象
     * @param clazz 要转化成的类型
     * @return 转化好的结果
     */
    public static  Object convert(String o, Class clazz) {

        if (clazz == Integer.class || clazz == int.class) {
            return Integer.parseInt(o);
        }
        if (clazz == Double.class || clazz == double.class) {
            return Double.valueOf(o);
        }
        if (clazz == Long.class || clazz == long.class) {
            return Long.valueOf(o);
        }
        if (clazz == Float.class || clazz == float.class) {
            return Float.valueOf(o);
        }
        if (clazz == Boolean.class || clazz == boolean.class) {
            return Boolean.valueOf(o);
        }
        return o;
    }
}
