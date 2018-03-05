package com.webmvc.helper;

import com.webmvc.annotation.*;
import com.webmvc.excepetion.WebMVCException;
import com.webmvc.proxy.AspectResolve;
import com.webmvc.proxy.CglibProxy;
import com.webmvc.proxy.bean.ProxyBean;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by A550V
 * 2018/3/2 21:15
 */
public final class AopHelper {
    static AspectResolve resolve = new AspectResolve();
    static Map<String, ProxyBean> proxyBeanMap = resolve.getProxyBeanMap();
    static {
        init();
    }
    static void init() {
        System.out.println("----开始AOP----");
        Collection<ProxyBean> proxyBeans = proxyBeanMap.values();
        for (ProxyBean proxyBean : proxyBeans) {
            CglibProxy cglibProxy = new CglibProxy(proxyBean);
            Object proxyObject = cglibProxy.getProxy();
            Object beanObject =proxyBean.getTargetObject();
            changeObject(proxyObject, beanObject);
        }
    }

    private static void changeObject(Object proxyObject, Object beanObject) {
        Map<String, Object> beanMap = BeanHelper.getBeanMap();
        Iterator<Map.Entry<String, Object>> iterator= beanMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
             Object o = entry.getValue();
            if (o.equals(beanObject)) {
                String key = entry.getKey();
                beanMap.put(key, proxyObject);
            }
        }
    }

}
