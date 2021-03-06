package com.webmvc;

import com.webmvc.annotation.RequestParam;
import com.webmvc.annotation.ResponseBody;
import com.webmvc.bean.Handler;
import com.webmvc.bean.ModelAndView;
import com.webmvc.excepetion.WebMVCException;
import com.webmvc.helper.BeanHelper;
import com.webmvc.helper.ConfigHelper;
import com.webmvc.helper.ControllerHelper;
import com.webmvc.helper.ConvertHelp;
import com.webmvc.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sgz
 * 2018/2/27 19:20
 */
public class DispatcherServlet extends HttpServlet{
	
	private static final long serialVersionUID = -6957112771305058960L;

	@Override
    public void init(ServletConfig config) throws ServletException {
        //初始化
        HelpLoader.init();
        //获取servletContext对象
        ServletContext servletContext = config.getServletContext();
        //注册处理jsp的servlet
        ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
        jspServlet.addMapping(ConfigHelper.getAppJspPath() + "*");
        //注册默认的servlet
        ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        defaultServlet.addMapping(ConfigHelper.getAppAssetPath() + "*");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //给HttpServletRequest和HttpServletResponse注值
	    Map<String, List<Field>> map = ControllerHelper.getRequestAndResponseFileds();
        List<Field> requestFields = map.get("request");
        List<Field> responseFields = map.get("response");
        if (CollectionUtil.isNotEmpty(requestFields)) {
            for (Field field :requestFields) {
                try {
                    //从bean容器中取对象，保证对象一致
                    ReflectionUtil.setFiled(BeanHelper.getBean(field.getDeclaringClass().getName()), field, req);
                } catch (Exception e) {
                    throw new WebMVCException("给field: "
                            + field.getDeclaringClass() + "中的"
                            + field.getName() + "注入HttpServletRequest时出错");
                }
            }
        }

        if (CollectionUtil.isNotEmpty(responseFields)) {
            for (Field field :responseFields) {
                try {
                    ReflectionUtil.setFiled(field.getDeclaringClass().newInstance(), field, resp);
                } catch (Exception e) {
                    throw new WebMVCException("给field: "
                            + field.getDeclaringClass() + "中的"
                            + field.getName() + "注入HttpServletResponse时出错");
                }
            }
        }


	    /*获取请求的路径和方法类型*/
        String requestMethod = req.getMethod().toLowerCase();
        String requestPath = req.getPathInfo();
        /*获取处理器*/
        Handler handler = null;
        try {
            handler = ControllerHelper.getHandler(requestMethod, requestPath);
        } catch (NullPointerException e) {
            resp.setContentType("text/html");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(404);
            PrintWriter writer = resp.getWriter();
            writer.write("没有找到请求路径, " + requestPath);
            writer.flush();
            writer.close();
        }
       

        if (handler != null) {
            //创建controller实例
            Class<?> controllerClass = handler.getControllerClass();
            Object controllerBean = BeanHelper.getBean(controllerClass.getName());
            //创建请求参数对象
            Map<String, Object> paramMap = new HashMap<>();
            Enumeration<String> paramNames = req.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                String paramValue = req.getParameter(paramName);
                paramMap.put(paramName, paramValue);
            }
            /*获取URL中的请求参数*/
            String body = CodecUtil.decodeURL(StreamUtil.getString(req.getInputStream()));
            if (StringUtil.isNotEmpty(body)) {
                String [] params = body.split("&");
                if (ArrayUtil.isNotEmpty(params)) {
                    for (String param : params) {
                        String [] array = param.split("=");
                        if (ArrayUtil.isNotEmpty(array) && array.length == 2) {
                            String paramName = array[0];
                            String paramValue = array[1];
                            paramMap.put(paramName, paramValue);
                        }
                    }
                }
            }
            /*请求要执行的方法*/
            Method mappingMethod = handler.getMappingMethod();
            /*获取参数*/
            Parameter[] parameters = mappingMethod.getParameters();
            Object[] pars = null;
            int i = 0;
            if (ArrayUtil.isNotEmpty(parameters)) {
                pars = new Object[parameters.length];
                for (Parameter p : parameters) {
                    if (p.isAnnotationPresent(RequestParam.class)) {
                        RequestParam rq = p.getAnnotation(RequestParam.class);
                        /*获取注解上的值*/
                        String v = rq.value();
                        boolean required = rq.required();
                        String defaultValue = rq.defaultValue();

                        Object requestValue = paramMap.get(v);
                        if (requestValue != null){
                            pars[i++] = ConvertHelp.convert(requestValue.toString(), p.getType());
                        } else {
                            if (required) {
                                pars[i++] = ConvertHelp.convert(defaultValue, p.getType());
                            } else {
                                pars[i++] = null;
                            }
                        }
                    }
                }
            }

            Object result = ReflectionUtil.invokeMethod(controllerBean, mappingMethod, pars);
            //是否包含ResponseBody注解
            boolean ResponseBody =  mappingMethod.isAnnotationPresent(ResponseBody.class);
            if ( ! ResponseBody) {
                //返回jsp
                ModelAndView view = (ModelAndView) result;
                String path = view.getView();
                if (StringUtil.isNotEmpty(path)) {
                    if (path.startsWith("/")) {
                        resp.sendRedirect(req.getContextPath() + path);
                    } else {
                        Map<String, Object> model = view.getModel();
                        for (Map.Entry<String, Object> entry : model.entrySet()) {
                            req.setAttribute(entry.getKey(), entry.getValue());
                        }
                        req.getRequestDispatcher(ConfigHelper.getAppJspPath() + path).forward(req, resp);
                    }
                }
            } else {
                //直接返回json
                Object model =  result;
                if (model != null) {
                    resp.setContentType("application/json"); //TODO
                    resp.setCharacterEncoding("UTF-8");
                    PrintWriter writer = resp.getWriter();
                    String json = JsonUtil.toJson(model);
                    writer.write(json);
                    writer.flush();
                    writer.close();
                }
            }
        } 
    }
}
