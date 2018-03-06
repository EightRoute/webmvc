package com.webmvc.proxy.bean;


import java.lang.reflect.Method;

/**
 * Created by A550V
 * 2018/3/4 15:48
 */
public class MethodParameter {

    //切面里的方法
    private Method adviceMethod;
    //切面对象  用来反射调用上面的adviceMethod
    private Object adviceObjectInstance;

    //要增强的目标方法名字
    private String targetMethodName;
    //要增强的目标方法返回类型
    private String targetMethodReturnType;





    public Method getAdviceMethod() {
        return adviceMethod;
    }
    public void setAdviceMethod(Method adviceMethod) {
        this.adviceMethod = adviceMethod;
    }
    public Object getAdviceObjectInstance() {
        return adviceObjectInstance;
    }
    public void setAdviceObjectInstance(Object adviceObjectInstance) {
        this.adviceObjectInstance = adviceObjectInstance;
    }

    public String getTargetMethodName() {
        return targetMethodName;
    }
    public void setTargetMethodName(String targetMethodName) {
        this.targetMethodName = targetMethodName;
    }
    public String getTargetMethodReturnType() {
        return targetMethodReturnType;
    }
    public void setTargetMethodReturnType(String targetMethodReturnType) {
        this.targetMethodReturnType = targetMethodReturnType;
    }

    @Override
    public String toString() {
        return "MethodParameter{" +
                "adviceMethod=" + adviceMethod +
                ", adviceObjectInstance=" + adviceObjectInstance +
                ", targetMethodName='" + targetMethodName + '\'' +
                ", targetMethodReturnType='" + targetMethodReturnType + '\'' +
                '}';
    }
}
