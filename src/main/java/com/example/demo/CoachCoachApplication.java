package com.example.demo;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import jakarta.annotation.PostConstruct;

@EnableMethodSecurity
@SpringBootApplication
public class CoachCoachApplication {
	
	@PostConstruct
	void started(){
	    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	public static void main(String[] args) {
		SpringApplication.run(CoachCoachApplication.class, args);
		
		System.out.println("0402 ");
	}
	

}
