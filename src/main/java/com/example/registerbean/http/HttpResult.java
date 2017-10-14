package com.example.registerbean.http;

/**
 * @author 李佳明
 * @date 2017.10.14
 */
public interface HttpResult<T> {

    T getResponse();

    int getStatus();
}
