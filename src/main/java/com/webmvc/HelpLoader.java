package com.webmvc;

import com.webmvc.helper.*;
import com.webmvc.util.ClassUtil;

/**
 * Created by A550V
 * 2018/2/26 21:44
 */
public final class HelpLoader {
    public static void init() {
        Class<?>[] classList = {
                ClassHelper.class,
                BeanHelper.class,
                AopHelper.class,
                IocHelper.class,
                ControllerHelper.class,

        };

        for (Class<?> clazz : classList) {
            ClassUtil.loadClass(clazz.getName(), true);
        }
    }

}
