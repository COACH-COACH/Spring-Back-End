// 2. 사용자의 모든 요청에 대해 JWTFilter가 사용자 인증 수행

package com.example.demo.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.model.dto.CustomUserDetails;
import com.example.demo.model.entity.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// JWTFilter: JWT 검증 필터 - 모든 HTTP 요청에 대해 JWT 토큰을 검증
// OncePerRequestFilter를 상속받아 구현
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // HTTP 요청의 Authorization 헤더에서 JWT 토큰을 추출
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization= request.getHeader("Authorization");

        // 토큰이 존재하지 않거나 형식이 맞지 않을 
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
		
        String token = authorization.split(" ")[1];
			
		//토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) {
            filterChain.doFilter(request, response);
            return;
        }

		// 토큰에서 username과 role 획득
        String loginId = jwtUtil.getUsername(token);
				
		// userEntity를 생성하여 값 set
        User userEntity = new User();
        userEntity.setLoginId(loginId);
        
        // 비번은 토큰에 담겨있지 않았지만, 매번 db에서 조회할 때마다 비번까지 같이 조회하는 것을 막기 위해 아무거나 입력해서 초기화함
        userEntity.setLoginPw("temppassword");
				
		// UserDetails에 회원 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

		// 스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
		
        // 세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}