package com.example.registerbean;

import com.example.registerbean.annotation.EnableHttpRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableHttpRequest
public class RegisterbeanImportBeanDefinitionRegistrarApplication {

	public static void main(String[] args) {
		SpringApplication.run(RegisterbeanImportBeanDefinitionRegistrarApplication.class, args);
	}
}
