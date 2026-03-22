package com.wastewatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling          // needed for Phase 7 background jobs
@EnableConfigurationProperties
public class WastewatchApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WastewatchApiApplication.class, args);
	}
}