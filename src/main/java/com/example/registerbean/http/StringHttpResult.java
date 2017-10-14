package com.example.registerbean.http;

import lombok.AllArgsConstructor;

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
