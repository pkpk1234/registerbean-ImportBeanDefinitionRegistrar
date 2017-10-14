package com.example.registerbean.http;

public interface HttpResult<T> {

    T getResponse();

    int getStatus();
}
