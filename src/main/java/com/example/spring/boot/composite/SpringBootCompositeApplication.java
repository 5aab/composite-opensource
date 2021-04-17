package com.example.spring.boot.composite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableTransactionManagement
public class SpringBootCompositeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootCompositeApplication.class, args);
	}

}
