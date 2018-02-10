package com.webmvc.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by A550V
 * 2018/2/10 22:51
 */
public class CollectionUtil {

    /**
     * 判断集合是否为空
     */
    public static Boolean isNotEmpty(Collection collection) {
       return ! collection.isEmpty();
    }

    public static Boolean isNotEmpty(Map map) {
        return ! map.isEmpty();
    }
}
