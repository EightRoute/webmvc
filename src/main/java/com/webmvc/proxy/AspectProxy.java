package com.webmvc.proxy;

import java.lang.reflect.Method;

/**
 * Created by A550V
 * 2018/3/1 21:49
 */
public abstract class AspectProxy implements Proxy{

    @Override
    public final Object doProxy(ProxyChain proxyChain) throws Throwable {
        Object result = null;
        Class<?> clazz = proxyChain.getTargetClass();
        Method method = proxyChain.getTargetMethod();
        Object[] params = proxyChain.getMethodParams();

        begin();
        try {
            if (intercept(clazz, method, params)) {
                before(clazz, method, params);
                result = proxyChain.doProxyChain();
                after(clazz, method, params, result);
            } else {
                result = proxyChain.doProxyChain();
            }
        } catch (Exception e) {
            error(clazz, method, params, e);
            throw e;
        } finally {
            end();
        }
        return null;
    }

    public void begin() {

    }

    public boolean intercept(Class<?> clazz, Method method, Object[] params) throws Throwable {
        return true;
    }

    public void before(Class<?> clazz, Method method, Object[] params) throws Throwable {

    }

    public void after(Class<?> clazz, Method method, Object[] params, Object result) throws Throwable {

    }

    public void error(Class<?> clazz, Method method, Object[] params, Throwable e) {

    }

    public void end() {

    }
}
