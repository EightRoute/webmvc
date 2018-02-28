package com.webmvc;

import com.webmvc.bean.Data;
import com.webmvc.bean.Handler;
import com.webmvc.bean.Param;
import com.webmvc.bean.View;
import com.webmvc.helper.BeanHelper;
import com.webmvc.helper.ConfigHelper;
import com.webmvc.helper.ControllerHelper;
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
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by A550V
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
            Param param = new Param(paramMap);
            /*请求要执行的方法*/
            Method mappingMethod = handler.getMappingMethod();

            Object result = ReflectionUtil.invokeMethod(controllerBean, mappingMethod, null);

            if (result instanceof View) {
                //返回jsp
                View view = (View) result;
                String path = view.getPath();
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
            } else if (result instanceof Data) {
                //直接返回json
                Data data = (Data) result;
                Object model = data.getModel();
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
