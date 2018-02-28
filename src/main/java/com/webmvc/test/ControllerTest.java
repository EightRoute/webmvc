package com.webmvc.test;

import com.webmvc.annotation.Controller;
import com.webmvc.annotation.RequestMapping;

/**
 * Created by A550V
 * 2018/2/26 19:54
 */

@Controller()
@RequestMapping(value = {"/user", "/sgz"})
public class ControllerTest {

    @RequestMapping(value = "/say")
    public String  say() {
        return "say";
    }
}
