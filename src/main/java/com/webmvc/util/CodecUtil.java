package com.webmvc.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webmvc.excepetion.WebMVCException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by A550V
 * 2018/2/26 22:16
 */
public class CodecUtil {

    /**
     * 将URL编码
     */
    public static String encodeURL(String source) {
        String target;
        try {
            target = URLEncoder.encode(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new WebMVCException("URL编码出错", e);
        }
        return target;
    }

    /**
     *将URL解码
     */
    public static String decodeURL(String source) {
        String target;
        try {
            target = URLDecoder.decode(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new WebMVCException("URL解码失败", e);
        }
        return target;
    }

}
