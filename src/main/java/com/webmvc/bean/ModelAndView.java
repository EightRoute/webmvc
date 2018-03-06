package com.webmvc.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by A550V
 * 2018/2/26 22:02
 */
public class ModelAndView {

    private String view;

    private Map<String, Object> model;

    public ModelAndView() {
    }

    public ModelAndView(String view) {
        this.view = view;
        model = new HashMap<>();
    }

    public ModelAndView addModel(String key, Object value) {
        model.put(key, value);
        return this;
    }
    public Map<String, Object> getModel() {
        return model;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }
}
