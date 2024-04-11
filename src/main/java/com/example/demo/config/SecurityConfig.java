package com.example.demo.config;


import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.example.demo.jwt.JWTFilter;
import com.example.demo.jwt.JWTUtil;
import com.example.demo.jwt.LoginFilter;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	//AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
	private final AuthenticationConfiguration authenticationConfiguration;
	private final JWTUtil jwtUtil;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
    }

	//AuthenticationManager Bean 등록
	@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
	
	@Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	
    	http
	        .cors((corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
	
	            @Override
	            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
	
	                CorsConfiguration configuration = new CorsConfiguration();
	
	                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:8081"));
	                configuration.setAllowedMethods(Collections.singletonList("*"));
	                configuration.setAllowCredentials(true);
	                configuration.setAllowedHeaders(Collections.singletonList("*"));
	                configuration.setMaxAge(3600L);
	
					configuration.setExposedHeaders(Collections.singletonList("Authorization"));
	
	                return configuration;
	            }
	        })));
    	
		//csrf disable
        http
                .csrf((auth) -> auth.disable());

		//From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

		//http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

		//경로별 인가 작업
//        http
//                .authorizeHttpRequests((auth) -> auth
//                        .requestMatchers("/login", "/", "/join").permitAll()
//												.requestMatchers("/admin").hasRole("ADMIN")
//                        .anyRequest().authenticated());

        http
		        .authorizeHttpRequests((auth) -> auth
		              .requestMatchers("/login", "/", "/user/join").permitAll() // 얘는 왜 /만 되는지 모르겠다.. '/login', 'user/join'안됨
		              .anyRequest().authenticated()); // 나머지 주소는 authenticated 된 것만 접근 가능
        
        //JWTFilter 등록
        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
        
        //필터 추가 LoginFilter()는 인자를 받음 (AuthenticationManager() 메소드에 authenticationConfiguration 객체를 넣어야 함) 따라서 등록 필요
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);
        
		//세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        
        

        return http.build();
    }
}