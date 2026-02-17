package com.bluesphere.map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SphereApplication {

	public static void main(String[] args) {
		SpringApplication.run(SphereApplication.class, args);
	}

}
