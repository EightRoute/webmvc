package com.webmvc.bean;


/**
 * Created by A550V
 * 2018/2/26 19:19
 */
public class Request {

    /*请求路径*/
    private String requestPath;

    public Request(String requestPath) {
        this.requestPath = requestPath;
    }

    public String getRequestPath() {
        return requestPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Request request = (Request) o;

        return requestPath != null ? requestPath.equals(request.requestPath) : request.requestPath == null;
    }

    @Override
    public int hashCode() {
        return requestPath != null ? requestPath.hashCode() : 0;
    }
}
