package com.example.registerbean.http;

import java.lang.reflect.Method;

/**
 * @author 李佳明
 * @date 2017.10.14
 */
public interface HTTPHandler {
    HttpResult<?> handle(Method method);
}
