package com.gymcore;

import com.gymcore.config.GymcoreProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(GymcoreProperties.class)
public class GymcoreBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(GymcoreBackendApplication.class, args);
	}

}
