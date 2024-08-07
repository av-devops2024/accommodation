package com.devops.accommodation;

import com.devops.accommodation.aspect.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
@EntityScan(basePackages = {"ftn.devops.db"})
public class AccommodationApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccommodationApplication.class, args);
	}

	@Bean
	public Gson transferService() {
		return new GsonBuilder()
				.setPrettyPrinting()
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
				.create();
	}
}
