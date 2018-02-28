package com.webmvc.helper;

import com.webmvc.annotation.RequestMapping;
import com.webmvc.bean.Handler;
import com.webmvc.bean.Request;
import com.webmvc.enums.RequestMethod;
import com.webmvc.util.ArrayUtil;
import com.webmvc.util.CollectionUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by A550V
 * 2018/2/26 19:47
 */
public final class ControllerHelper {
    private static final Map<Request, Handler> REQUEST_MAP = new HashMap<>();

    private static final Map<String, List<Field>> REQUEST_AND_RESPONSE_FILEDS = new HashMap<>();

    static {
        Set<Class<?>> controllerClassSet = ClassHelper.getControllerClassSet();
        if (CollectionUtil.isNotEmpty(controllerClassSet)) {
            List<Field> req = new ArrayList<>();
            List<Field> resp = new ArrayList<>();
            /*遍历包含controller注解的类，将HttpServletRequest和HttpServletResponse的域添加到集合中*/
            for (Class<?> controllerClass : controllerClassSet) {
                if (controllerClass.isAnnotationPresent(RequestMapping.class)) {
                    Field[] fields =  controllerClass.getDeclaredFields();
                    for (Field field : fields) {
                        if (field.getType() == HttpServletRequest.class) {
                            req.add(field);
                        }
                        if (field.getType() == HttpServletResponse.class) {
                            resp.add(field);
                        }
                    }


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
                                    if (baseValue.equals("/")) {
                                        baseValue = "";
                                    }
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
            /*将HttpServletRequest和HttpServletResponse存起来等待取到值后注入*/
            REQUEST_AND_RESPONSE_FILEDS.put("request", req);
            REQUEST_AND_RESPONSE_FILEDS.put("response", resp);
        }
    }

    /**
     * 根据请求路径和方法找到要映射到的类和方法
     * @param requestMethod 请求的方法类型(如get,post)
     * @param requestPath 请求路径
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

    /**
     * @return Controller中HttpServletRequest和HttpServletResponse的域
     * HttpServletRequest的key为request
     * HttpServletResponse的key为response
     */
    public static Map<String, List<Field>> getRequestAndResponseFileds() {
        return REQUEST_AND_RESPONSE_FILEDS;
    }
}
