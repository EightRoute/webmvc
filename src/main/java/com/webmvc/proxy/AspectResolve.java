package com.webmvc.proxy;

import com.webmvc.annotation.*;
import com.webmvc.helper.BeanHelper;
import com.webmvc.helper.ClassHelper;
import com.webmvc.proxy.bean.MethodParameter;
import com.webmvc.proxy.bean.PointcutBean;
import com.webmvc.proxy.bean.ProxyBean;
import com.webmvc.util.ReflectionUtil;


import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by A550V
 * 2018/3/4 23:10
 */
public class AspectResolve {
    /**
     * 1得到所有包含Aspect注解的类
     * 2得到包含Pointcut注解的方法
     * 3解析Pointcut注解里的表达式,得到目标类
     * 4根据目标类得到容器中的bean对象，并生成Map<表达式，ProxyBean>
     * 5生成ProxyBean对象加入到map中
     *
     * 6得到Before、After、AfterThrowing、AfterReturning注解的方法
     * 7判断注解是否为表达式，如果是则执行45，
     * 并将改方法添加到ProxyBean对象中，如果不是则直接将该方法添加到ProxyBean对象中
     */
    static Map<String, ProxyBean> proxyBeanMap = new HashMap<>();
    static Map<String, PointcutBean> pointcutBeanMap = new HashMap<>();
    static Map<String, PointcutBean> pointcutNameMap = new HashMap<>();

    static {
        new AspectResolve().resolveAnnotation();
    }
    public  void resolveAnnotation (){
        Set<Class<?>> classSet  = ClassHelper.getClassSetByAnnotation(Aspect.class);

        for (Class<?> clazz : classSet) {
            Object aspectInstance =  ReflectionUtil.newInstance(clazz);
            Method[] methods = clazz.getDeclaredMethods();
            //找到所有Pointcut
            for (Method method : methods) {
                if (method.isAnnotationPresent(Pointcut.class)) {
                    Pointcut pointcut = method.getAnnotation(Pointcut.class);
                    String expression = pointcut.value();
                    //如果为pointcut表达式
                    if (isPointcutExpression(expression)) {
                        PointcutBean pointcutBean = resolveExpression(expression);
                        String beanClassName = pointcutBean.getTargetBeanName();
                        ProxyBean proxyBean = new ProxyBean();
                        proxyBean.setTargetObject(getBeanByClassName(beanClassName));
                        proxyBeanMap.put(beanClassName, proxyBean);
                        pointcutBeanMap.put(expression, pointcutBean);
                        pointcutNameMap.put(method.getName() + "()", pointcutBean);
                    }
                }
            }

            for (Method method : methods) {
                if (method.isAnnotationPresent(Before.class)) {
                    Before before = method.getAnnotation(Before.class);
                    String beforeExpression = before.value();
                    resolveMethodAnnotation(beforeExpression, method, aspectInstance, "before");
                }
                if (method.isAnnotationPresent(After.class)) {
                    After after = method.getAnnotation(After.class);
                    String afterExpression = after.value();
                    resolveMethodAnnotation(afterExpression, method, aspectInstance, "after");
                }
                if (method.isAnnotationPresent(AfterThrowing.class)) {
                    AfterThrowing afterThrowing = method.getAnnotation(AfterThrowing.class);
                    String afterThrowingExpression = afterThrowing.value();
                    resolveMethodAnnotation(afterThrowingExpression, method, aspectInstance, "afterThrowing");
                }
                if (method.isAnnotationPresent(AfterReturning.class)) {
                    AfterReturning afterReturning = method.getAnnotation(AfterReturning.class);
                    String afterReturningExpression = afterReturning.value();
                    resolveMethodAnnotation(afterReturningExpression, method, aspectInstance, "afterReturning");
                }
            }

        }
    }

    /**
     * 生成ProxyBean对象 用来获取代理对象
     * @param expression Pointcut注解的值
     * @param method 要增强的方法
     * @param aspectInstance 切面类的对象
     * @param s 增强类型 before afterdeng
     */
    private void resolveMethodAnnotation(String expression, Method method, Object aspectInstance, String s) {
        if (isPointcutExpression(expression)) {
            PointcutBean pointcutBean = resolveExpression(expression);
            String beanClassName = pointcutBean.getTargetBeanName();
            if (proxyBeanMap.get(beanClassName) == null) {
                ProxyBean proxyBean = new ProxyBean();
                proxyBean.setTargetObject(getBeanByClassName(beanClassName));
                MethodParameter methodParameter = new MethodParameter();
                methodParameter.setTargetMethodName(pointcutBean.getTargetMethodName());
                methodParameter.setTargetMethodReturnType(pointcutBean.getTargetReturnType());
                methodParameter.setAdviceMethod(method);
                methodParameter.setAdviceObjectInstance(aspectInstance);
                addMethodParameter(proxyBean, s, methodParameter);
                proxyBeanMap.put(beanClassName, proxyBean);
            } else {
                //如果存在相同的切点
                ProxyBean proxyBean = proxyBeanMap.get(beanClassName);
                proxyBean.setTargetObject(getBeanByClassName(beanClassName));
                MethodParameter methodParameter = new MethodParameter();
                methodParameter.setTargetMethodName(pointcutBean.getTargetMethodName());
                methodParameter.setTargetMethodReturnType(pointcutBean.getTargetReturnType());
                methodParameter.setAdviceObjectInstance(aspectInstance);
                methodParameter.setAdviceMethod(method);
                addMethodParameter(proxyBean, s, methodParameter);
                proxyBeanMap.put(beanClassName, proxyBean);
            }
        } else {
            //如果表达式为切点的名字
            PointcutBean pointcutBean = pointcutNameMap.get(expression);
            String beanClassName = pointcutBean.getTargetBeanName();
            ProxyBean proxyBean = proxyBeanMap.get(beanClassName);
            MethodParameter methodParameter = new MethodParameter();
            methodParameter.setTargetMethodName(pointcutBean.getTargetMethodName());
            methodParameter.setTargetMethodReturnType(pointcutBean.getTargetReturnType());
            methodParameter.setAdviceObjectInstance(aspectInstance);
            methodParameter.setAdviceMethod(method);
            addMethodParameter(proxyBean, s, methodParameter);
            proxyBeanMap.put(beanClassName, proxyBean);
        }
    }

    //偷个懒
    private void addMethodParameter(ProxyBean proxyBean, String s, MethodParameter methodParameter) {
        if (s.equals("before")) {
            proxyBean.addBeforeMethodParameter(methodParameter);
        } else if (s.equals("after")) {
            proxyBean.addAfterMethodParameters(methodParameter);
        } else if (s.equals("afterThrowing")) {
            proxyBean.addAfterThrowingMethodParameter(methodParameter);
        } else if (s.equals("afterReturning")) {
            proxyBean.addAfterReturningMethodParameters(methodParameter);
        }
    }

    private  Object getBeanByClassName(String beanClassName) {
        Map<String, Object> beanMap = BeanHelper.getBeanMap();
        Collection<Object> beanObject = beanMap.values();
        for (Object o : beanObject) {
            if (o.getClass().getName().equals(beanClassName)) {
                return o;
            }
        }
        return null;
    }



    private  boolean isPointcutExpression(String expression) {
        return expression.startsWith("execution");
    }


    //解析Pointcut表达式
    private PointcutBean resolveExpression(String expression) {
        String oldExpression = expression;
        expression = expression.substring(10, expression.length()-1);
        String[] s = expression.split(" ");
        String methodExpress = s[1];
        int splitIndex = methodExpress.lastIndexOf(".");
        String targetReturnType = s[0];
        String targetBeanName = methodExpress.substring(0, splitIndex);
        String targetMethodName = methodExpress.substring(splitIndex + 1, methodExpress.length());
        PointcutBean pointcutBean = new PointcutBean();
        pointcutBean.setTargetReturnType(targetReturnType);
        pointcutBean.setTargetBeanName(targetBeanName);
        pointcutBean.setTargetMethodName(targetMethodName.substring(0, targetMethodName.length() - 2));
        pointcutBean.setExpression(oldExpression);
        return pointcutBean;
    }


    public  Map<String, ProxyBean> getProxyBeanMap() {
        return proxyBeanMap;
    }

}
