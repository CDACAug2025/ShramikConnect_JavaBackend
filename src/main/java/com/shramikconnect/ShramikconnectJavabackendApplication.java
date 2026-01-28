package com.shramikconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ShramikconnectJavabackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShramikconnectJavabackendApplication.class, args);
	}

}
