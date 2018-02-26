package com.webmvc.bean;

import com.webmvc.enums.RequestMethod;

import java.lang.reflect.Method;

/**
 * Created by A550V
 * 2018/2/26 19:23
 */
public class Handler {
    /*controller类*/
    private Class<?> controllerClass;

    /*RequestMapping*/
    private Method mappingMethod;

    /*请求方法*/
    private RequestMethod[] requestMethods;

    public Handler(Class<?> controllerClass, Method mappingMethod, RequestMethod[] requestMethods) {
        this.controllerClass = controllerClass;
        this.mappingMethod = mappingMethod;
        this.requestMethods = requestMethods;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public Method getMappingMethod() {
        return mappingMethod;
    }

    public RequestMethod[] getRequestMethods() {
        return requestMethods;
    }
}
