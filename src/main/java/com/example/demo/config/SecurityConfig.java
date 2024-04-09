package com.example.demo.config;

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

import com.example.demo.jwt.JWTFilter;
import com.example.demo.jwt.JWTUtil;
import com.example.demo.jwt.LoginFilter;

// Spring Security를 이용하여 보안 설정을 정의하는 클래스
@Configuration
@EnableWebSecurity // Spring Security 설정을 활성화
public class SecurityConfig {

	// *AuthenticationManager: Spring Security에서 인증을 관리하는 주요 인터페이스
	// AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
	private final AuthenticationConfiguration authenticationConfiguration;
	private final JWTUtil jwtUtil;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
    }

	// AuthenticationManager Bean 등록
	@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
	
	// 비밀번호 암호화에 사용되는 클래스
	@Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

	// HTTP 보안 관련 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// CSRF 보호 기능 비활성화(세션 방식은 필요하나, JWT는 세션을 STATELESS로 관리하기 때문에 필요X)
        http.csrf((auth) -> auth.disable());

		// FORM 기반 로그인 비활성
        http.formLogin((auth) -> auth.disable());

		// HTTP 기본 인증 비활성화 = JWT 기반 인증을 사용하겠다
        http.httpBasic((auth) -> auth.disable());

        // 인가 작업
        // 해당 경로("/login", "/", "/user/join")에 대해 모든 사용자의 접근 허용
        http.authorizeHttpRequests((auth) -> auth
		              .requestMatchers("/login", "/", "/user/join").permitAll() // 얘는 왜 /만 되는지 모르겠다.. '/login', 'user/join'안됨
		              .anyRequest().authenticated()); // 나머지 주소는 authenticated 된 것만 접근 가능
        
        // 'JWTFilter'를 필터 체인에 등록 => 요청이 들어올 때마다 JWT 토큰의 유효성을 검증하도록 
        http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
        
        // (1) addFilterAt으로 우리가 만든 LoginFilter를 등록
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);
        
		// 세션 정책을 STATELESS로 설정: 세션을 사용하지 않고, 각 요청마다 인증을 확인
        // = RESTful API 서비스에 적합한 설정 = JWT 토큰 기반 인증 방식과 잘 맞음
        http.sessionManagement((session) -> session
        		.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}