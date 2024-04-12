// 1. 사용자가 로그인을 시도할 때 LoginFilter가 활성화 됨

package com.example.demo.jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.model.dto.CustomUserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// LoginFilter: 사용자 로그인 시도를 처리하는 필터
// UsernamePasswordAuthenticationFilter를 상속받아 구현
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
  	private final JWTUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    // 1) 로그인 시도
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 클라이언트로부터 받은 loginId와 loginPw를 이용해
        String loginId = request.getParameter("loginId");
        String loginPw = request.getParameter("loginPw");
        
        // UsernamePasswordAuthenticationToken 객체 생성: 아직 인증되지 않은 상태의 토큰
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginId, loginPw, null); // 3번째 인자 = roll(우린 없음)

        // AuthenticationManager에게 전달해 인증 시도
        return authenticationManager.authenticate(authToken);
    }

	// 2-1) 인증에 성공한 경우
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
    	
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String loginId = customUserDetails.getUsername();
        
        // loginId를 기반으로 JWT 토큰 생성(토큰의 유효기간 포함)
        String token = jwtUtil.createJwt(loginId, 1000 * 60L * 10 * 10); // 100분
        System.out.println(token);
        
        // HTTP 응답 헤더에 Authorization 필드로 추가하여 클라이언트에게 전달
        response.addHeader("Authorization", "Bearer " + token);
        
    }

	// 2-2) 인증에 실패한 경우
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
    	// HTTP 응답 상태 코드를 401(Unauthorized)로 설정
    	response.setStatus(401);
    }
}