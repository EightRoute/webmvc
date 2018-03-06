package com.webmvc.proxy;


import java.lang.reflect.Method;
import java.util.List;

import com.webmvc.excepetion.WebMVCException;
import com.webmvc.proxy.bean.MethodParameter;
import com.webmvc.proxy.bean.ProxyBean;
import com.webmvc.util.CollectionUtil;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * Created by A550V
 * 2018/3/5 20:24
 * 使用cglib生成代理对象
 * cglib使用继承 JDK使用接口  JDK需要接口
 * cglib生成的代理类继承自被代理类/目标类，请求时，执行自己织入的增强，
 * 然后再执行目标方法，因而使用继承方式创建代理类不能代理任何final方法和类，
 * 以及静态方法
 */
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

    /**
     * @return 生成的代理对象
     */
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
                            throw new WebMVCException("调用方法时出错， method: " + m.getName(), e);
                        }
                    }
                }
            }
        }
    }

}
