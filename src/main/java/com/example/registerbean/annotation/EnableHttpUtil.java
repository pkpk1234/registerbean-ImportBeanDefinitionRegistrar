package com.example.registerbean.annotation;

import com.example.registerbean.http.HTTPRequestRegistrar;
import org.springframework.context.annotation.Import;

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
@Target(ElementType.TYPE)
@Import(HTTPRequestRegistrar.class)
public @interface EnableHttpUtil {
}
