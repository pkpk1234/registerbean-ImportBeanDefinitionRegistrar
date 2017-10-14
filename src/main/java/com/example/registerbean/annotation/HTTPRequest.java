package com.example.registerbean.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 李佳明
 * @date 2017.10.14
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HTTPRequest {
    HTTPMethod httpMethod() default HTTPMethod.GET;
    String url();
}
