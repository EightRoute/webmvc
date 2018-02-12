package com.webmvc.util;



/**
 * Created by sgz
 * 2018/2/10 23:02
 */
public class ArrayUtil {

    /**
     *判断数组是否为空
     */
    public static <T> Boolean isNotEmpty(final T[] objs) {
        return objs != null && objs.length > 0;
    }

    public static  Boolean isNotEmpty(final int[] objs) {
        return objs != null && objs.length > 0;
    }

    public static  Boolean isNotEmpty(final long[] objs) {
        return objs != null && objs.length > 0;
    }

    public static  Boolean isNotEmpty(final short[] objs) {
        return objs != null && objs.length > 0;
    }

    public static  Boolean isNotEmpty(final double[] objs) {
        return objs != null && objs.length > 0;
    }

    public static  Boolean isNotEmpty(final float[] objs) {
        return objs != null && objs.length > 0;
    }

    public static  Boolean isNotEmpty(final char[] objs) {
        return objs != null && objs.length > 0;
    }

    public static  Boolean isNotEmpty(final byte[] objs) {
        return objs != null && objs.length > 0;
    }

    public static  Boolean isNotEmpty(final boolean[] objs) {
        return objs != null && objs.length > 0;
    }
}
