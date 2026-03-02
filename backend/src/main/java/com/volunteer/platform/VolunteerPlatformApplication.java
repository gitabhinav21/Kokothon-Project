package com.volunteer.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class VolunteerPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(VolunteerPlatformApplication.class, args);
	}

}
