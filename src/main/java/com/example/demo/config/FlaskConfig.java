package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlaskConfig {
	@Value("${spring.flask.url}")
	public String flaskUrl;
	
	@Value("${vue.url}")
	public String vueUrl;

}
