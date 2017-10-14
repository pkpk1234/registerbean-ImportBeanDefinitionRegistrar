package com.example.registerbean.http;

import com.example.registerbean.annotation.HTTPRequest;

import java.lang.reflect.Method;

/**
 * @author 李佳明
 * @date 2017.10.14
 */
public class DemoHttpHandler implements HTTPHandler {

    @Override
    public HttpResult<?> handle(Method method) {
        HTTPRequest request = method.getAnnotation(HTTPRequest.class);
        String url = request.url();
        String methodName = request.httpMethod().name();
        String str = String.format("http request: url=%s and method=%s", url, methodName);
        return new StringHttpResult(str, 200);
    }
}
