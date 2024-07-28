package com.devops.accommodation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
@EntityScan(basePackages = {"ftn.devops.db"})
public class AccommodationApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccommodationApplication.class, args);
	}

}
