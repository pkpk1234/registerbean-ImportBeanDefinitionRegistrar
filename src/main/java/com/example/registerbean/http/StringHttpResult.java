package com.example.registerbean.http;

import lombok.AllArgsConstructor;

/**
 * @author 李佳明
 * @date 2017.10.14
 */
@AllArgsConstructor
public class StringHttpResult implements HttpResult<String> {
    private String result;
    private int status;

    @Override
    public String getResponse() {
        return this.result;
    }

    @Override
    public int getStatus() {
        return this.status;
    }
}
