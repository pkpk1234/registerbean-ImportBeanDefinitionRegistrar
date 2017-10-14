package com.example.registerbean;

import com.example.registerbean.annotation.EnableHttpUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 李佳明
 * @date 2017.10.14
 */
@SpringBootApplication
@EnableHttpUtil
public class RegisterbeanImportBeanDefinitionRegistrarApplication {

	public static void main(String[] args) {
		SpringApplication.run(RegisterbeanImportBeanDefinitionRegistrarApplication.class, args);
	}
}
