package com.webmvc.proxy;

/**
 * Created by A550V
 * 2018/3/5 20:24
 */
import java.lang.reflect.Method;
import java.util.List;

import com.webmvc.proxy.bean.MethodParameter;
import com.webmvc.proxy.bean.ProxyBean;
import com.webmvc.util.CollectionUtil;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CglibProxy {

    //要被代理的对象
    private Object targetObject;
    //前置方法
    private List<MethodParameter> beforeMethodParameters;
    //后置方法
    private List<MethodParameter> afterMethodParameters;
    private List<MethodParameter> afterThrowingMethodParameters;
    private List<MethodParameter> afterReturningMethodParameters;


    public CglibProxy(ProxyBean proxyBean) {
        this.targetObject = proxyBean.getTargetObject();
        this.beforeMethodParameters = proxyBean.getBeforeMethodParameters();
        this.afterMethodParameters = proxyBean.getAfterMethodParameters();
        this.afterThrowingMethodParameters = proxyBean.getAfterThrowingMethodParameters();
        this.afterReturningMethodParameters = proxyBean.getAfterReturningMethodParameters();
    }

    public Object getProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetObject.getClass());
        enhancer.setCallback(new MethodInterceptor() {

            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                Object result;
                before(method);
                try {
                    result = proxy.invokeSuper(obj, args);
                    afterReturning(method, result);
                } catch (Exception e) {
                    afterThrowing(method, e);
                    throw new RuntimeException();
                } finally {
                    after(method);
                }
                return result;
            }
        });
        return enhancer.create();
    }

    private void before(Method targetMethod) {
        invokeAdviceMethod(targetMethod, beforeMethodParameters, null);
    }

    private void after(Method targetMethod) {
        invokeAdviceMethod(targetMethod, afterMethodParameters, null);
    }

    private void afterThrowing(Method targetMethod, Exception e) {
        invokeAdviceMethod(targetMethod, afterThrowingMethodParameters, e);
    }

    private void afterReturning(Method targetMethod, Object result) {
        invokeAdviceMethod(targetMethod, afterReturningMethodParameters, result);
    }

    private void invokeAdviceMethod(Method targetMethod, List<MethodParameter> methodParameters, Object... args) {
        if (CollectionUtil.isNotEmpty(methodParameters)) {
            for (MethodParameter methodParameter : methodParameters) {

                if (targetMethod.getName().equals(methodParameter.getTargetMethodName())) {
                    if (methodParameter.getTargetMethodReturnType().equals("*") || methodParameter.getTargetMethodReturnType().equals(targetMethod.getReturnType().getSimpleName())) {
                        Method m = methodParameter.getAdviceMethod();
                        Object instance = methodParameter.getAdviceObjectInstance();
                        try {
                            m.invoke(instance, args);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

}
