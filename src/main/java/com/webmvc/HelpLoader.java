package com.webmvc;

import com.webmvc.helper.BeanHelper;
import com.webmvc.helper.ClassHelper;
import com.webmvc.helper.ControllerHelper;
import com.webmvc.helper.IocHelper;
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
                IocHelper.class,
                ControllerHelper.class
        };

        for (Class<?> clazz : classList) {
            ClassUtil.loadClass(clazz.getName(), true);
        }
    }

}
