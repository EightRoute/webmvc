package com.webmvc.test;

import com.webmvc.bean.Handler;
import com.webmvc.enums.RequestMethod;
import com.webmvc.helper.ControllerHelper;

/**
 * Created by A550V
 * 2018/2/26 19:55
 */
public class Main {

    public static void main(String[] args) {
        ControllerHelper controllerHelper =  new ControllerHelper();
        Handler handler = ControllerHelper.getHandler("POST", "/sgz/say");
        System.out.println(handler.getControllerClass().getName());
        System.out.println(handler.getMappingMethod().getName());
    }
}
