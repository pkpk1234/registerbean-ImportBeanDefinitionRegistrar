package com.example.registerbean;

import com.example.registerbean.annotation.HTTPMethod;
import com.example.registerbean.annotation.HTTPRequest;
import com.example.registerbean.annotation.HTTPUtil;
import com.example.registerbean.http.HttpResult;
import org.springframework.stereotype.Component;

/**
 * @author 李佳明
 * @date 2017.10.14
 */
@Component
@HTTPUtil
public interface IRequestDemo {

    @HTTPRequest(url = "http://abc.com")
    HttpResult<String> test1();

    @HTTPRequest(url = "http://test2.com", httpMethod = HTTPMethod.POST)
    HttpResult<String> test2();
}
