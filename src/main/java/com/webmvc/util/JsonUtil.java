package com.webmvc.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webmvc.excepetion.WebMVCException;

import java.io.IOException;

/**
 * Created by A550V
 * 2018/2/26 22:25
 */
public final class JsonUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    /**
     * pojo转换为json
     */
    public static <T> String toJson(T obj){
        String json;
        try {
            json = OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new WebMVCException("pojo转换为json失败", e);
        }
        return json;
    }



    /**
     *json转换为pojo
     */
    public static <T> T fromJson(String json, Class<T> type) {
        T pojo;
        try {
            pojo = OBJECT_MAPPER.readValue(json, type);
        } catch (IOException e) {
            throw new WebMVCException("json转换为pojo出错", e);
        }
        return pojo;
    }
} 
