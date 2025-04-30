package com.example.fakenewsdetection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.fakenewsdetection")
public class FakenewsdetectionApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		System.setProperty("google.factcheck.api.key", dotenv.get("GOOGLE_FACTCHECK_API_KEY"));
		SpringApplication.run(FakenewsdetectionApplication.class, args);
	}

}
