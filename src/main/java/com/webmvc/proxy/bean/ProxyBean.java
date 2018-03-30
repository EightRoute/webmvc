package com.webmvc.proxy.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgz
 * 2018/3/4 23:02
 * 要根据对象生成代理对象
 */
public class ProxyBean {

    //要被代理的对象
    private Object targetObject;

    //前置方法
    private List<MethodParameter> beforeMethodParameters = new ArrayList<>();
    //后置方法
    private List<MethodParameter> afterMethodParameters = new ArrayList<>();
    private List<MethodParameter> afterThrowingMethodParameters = new ArrayList<>();
    private List<MethodParameter> afterReturningMethodParameters = new ArrayList<>();

    public void addBeforeMethodParameter (MethodParameter beforeMethodParameter) {
        beforeMethodParameters.add(beforeMethodParameter);
    }

    public void addAfterMethodParameters (MethodParameter afterMethodParameter) {
        afterMethodParameters.add(afterMethodParameter);
    }

    public void addAfterThrowingMethodParameter (MethodParameter afterThrowingMethodParameter) {
        afterThrowingMethodParameters.add(afterThrowingMethodParameter);
    }

    public void addAfterReturningMethodParameters (MethodParameter afterReturningMethodParameter) {
        afterReturningMethodParameters.add(afterReturningMethodParameter);
    }

    public Object getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(Object targetObject) {
        this.targetObject = targetObject;
    }

    public List<MethodParameter> getBeforeMethodParameters() {
        return beforeMethodParameters;
    }

    public void setBeforeMethodParameters(List<MethodParameter> beforeMethodParameters) {
        this.beforeMethodParameters = beforeMethodParameters;
    }

    public List<MethodParameter> getAfterMethodParameters() {
        return afterMethodParameters;
    }

    public void setAfterMethodParameters(List<MethodParameter> afterMethodParameters) {
        this.afterMethodParameters = afterMethodParameters;
    }

    public List<MethodParameter> getAfterThrowingMethodParameters() {
        return afterThrowingMethodParameters;
    }

    public void setAfterThrowingMethodParameters(List<MethodParameter> afterThrowingMethodParameters) {
        this.afterThrowingMethodParameters = afterThrowingMethodParameters;
    }

    public List<MethodParameter> getAfterReturningMethodParameters() {
        return afterReturningMethodParameters;
    }

    public void setAfterReturningMethodParameters(List<MethodParameter> afterReturningMethodParameters) {
        this.afterReturningMethodParameters = afterReturningMethodParameters;
    }

    @Override
    public String toString() {
        return "ProxyBean{" +
                "targetObject=" + targetObject +
                ", beforeMethodParameters=" + beforeMethodParameters +
                ", afterMethodParameters=" + afterMethodParameters +
                ", afterThrowingMethodParameters=" + afterThrowingMethodParameters +
                ", afterReturningMethodParameters=" + afterReturningMethodParameters +
                '}';
    }
}
