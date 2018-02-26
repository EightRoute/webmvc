package com.webmvc.util;

import com.webmvc.excepetion.WebMVCException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by A550V
 * 2018/2/26 22:06
 */
public final class StreamUtil {
    /**
     *从输入流中获取字符串
     */
    public static String getString(InputStream is) {
        StringBuilder sb = new StringBuilder();

        BufferedReader reader =  new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            while ((line =  reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw new WebMVCException("从输入流中获取字符串失败", e);
        }
        return sb.toString();
    }


}
