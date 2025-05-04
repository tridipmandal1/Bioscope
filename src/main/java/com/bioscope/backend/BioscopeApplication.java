package com.bioscope.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BioscopeApplication {

	public static void main(String[] args) {
		SpringApplication.run(BioscopeApplication.class, args);
	}

}
