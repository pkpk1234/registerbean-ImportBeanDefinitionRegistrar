package com.example.registerbean.http;

import java.lang.reflect.Method;

public interface HTTPHandler {
    HttpResult<?> handle(Method method);
}
