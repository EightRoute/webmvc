package com.webmvc.helper;

import com.webmvc.annotation.RequestMapping;
import com.webmvc.bean.Handler;
import com.webmvc.bean.Request;
import com.webmvc.enums.RequestMethod;
import com.webmvc.util.ArrayUtil;
import com.webmvc.util.CollectionUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by A550V
 * 2018/2/26 19:47
 */
public final class ControllerHelper {
    private static final Map<Request, Handler> REQUEST_MAP = new HashMap<>();

    static {
        Set<Class<?>> controllerClassSet = ClassHelper.getControllerClassSet();
        if (CollectionUtil.isNotEmpty(controllerClassSet)) {
            /*遍历包含controller注解的类*/
            for (Class<?> controllerClass : controllerClassSet) {
                if (controllerClass.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = controllerClass.getAnnotation(RequestMapping.class);
                    //类上面的RequestMapping注解
                    RequestMethod[] baseMethods = requestMapping.method();
                    String[] baseValues = requestMapping.value();

                    Method[] allMethods = controllerClass.getMethods();
                    for (Method requestMethod : allMethods) {
                        //找出包含RequestMapping注解的方法
                        if (requestMethod.isAnnotationPresent(RequestMapping.class)) {
                            RequestMapping methodMapping = requestMethod.getAnnotation(RequestMapping.class);
                            //方法上面的RequestMapping注解
                            RequestMethod[] method = methodMapping.method();
                            String[] values = methodMapping.value();
                            for (String baseValue : baseValues) {
                                for (String value : values) {
                                    //将类上的请求路径和方法上的请求路径拼接起来
                                    String requestPath  = baseValue + value;
                                    RequestMethod[] requestMethods;
                                    if (ArrayUtil.isNotEmpty(method)) {
                                        //如果方法上的不为空，则以方法上的注解
                                        requestMethods = method;
                                    } else  if (ArrayUtil.isNotEmpty(baseMethods)) {
                                        //如果方法上的为空，则以类上的注解
                                        requestMethods = baseMethods;
                                    } else {
                                        //如果都为空，则默认全部
                                        requestMethods = RequestMethod.getAll();
                                    }
                                    Request request = new Request(requestPath);
                                    Handler handler = new Handler(controllerClass, requestMethod, requestMethods);
                                    REQUEST_MAP.put(request, handler);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 根据请求路径和方法找到要映射到的类和方法
     * @param requestMethod
     * @param requestPath
     * @return 包含类和方法的Handler
     */
    public static Handler getHandler(String requestMethod, String requestPath) {
        Request request = new Request(requestPath);
        Handler handler = REQUEST_MAP.get(request);
        RequestMethod[] supportiveMethods = handler.getRequestMethods();
        for (RequestMethod supportiveMethod : supportiveMethods) {
            if (supportiveMethod.name().equalsIgnoreCase(requestMethod)) {
                return handler;
            }
        }
        return null;
    }
}
