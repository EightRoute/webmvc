package com.webmvc.proxy;

/**
 * Created by A550V
 * 2018/3/1 21:27
 */
public interface Proxy {
    Object doProxy(ProxyChain proxyChain) throws Throwable;
}
