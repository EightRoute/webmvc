package com.webmvc.bean;

import com.webmvc.util.CastUtil;

import java.util.Map;

/**
 * Created by A550V
 * 2018/2/26 21:48
 */
public class Param {

    private Map<String, Object> paramMap;

    public Param(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    public long getLong(String name) {
        return CastUtil.castLong(paramMap.get(name));
    }

    public Map<String, Object> getMap() {
        return paramMap;
    }
}
