package com.example.online_learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class OnlineLearningApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineLearningApplication.class, args);
	}

}
