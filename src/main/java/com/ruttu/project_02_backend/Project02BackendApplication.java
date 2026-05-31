package com.ruttu.project_02_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Project02BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(Project02BackendApplication.class, args);
	}

}
