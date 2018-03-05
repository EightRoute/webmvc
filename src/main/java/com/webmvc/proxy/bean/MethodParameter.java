package com.webmvc.proxy.bean;


import java.lang.reflect.Method;

public class MethodParameter {

    //切面
    private Method adviceMethod;
    private Object adviceObjectInstance;

    //切点
    private String targetMethodName;
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
