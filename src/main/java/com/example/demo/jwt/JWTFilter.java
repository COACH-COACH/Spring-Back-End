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

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
				
		//request에서 Authorization 헤더를 찾음
        String authorization= request.getHeader("Authorization");
				
		//Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {

            System.out.println("token null"); // 처음 로그인을 할 때는 token null이 출력되는 것이 맞나??
            filterChain.doFilter(request, response); // 다음 필터로 넘긴다
						
			//조건이 해당되면 메소드 종료 (필수)
            return;
        }
			
        System.out.println("authorization now");
		//Bearer 부분 제거 후 순수 토큰만 획득
        String token = authorization.split(" ")[1];
//        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        System.out.println(token);
        //eyJhbGciOiJIUzI1NiJ9.eyJsb2dpbklkIjoiYWRtaW4iLCJpYXQiOjE3MTIxMjc2NjYsImV4cCI6MTcxMjEyNzcwMn0.H1luxy9HZvPNMhO81rwl0nq0YqKysdyiy3CWJHCG-J0
//        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			
		//토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) {

            System.out.println("token expired");
            filterChain.doFilter(request, response);

			//조건이 해당되면 메소드 종료 (필수)
            return;
        }

				//토큰에서 username과 role 획득
        String loginId = jwtUtil.getUsername(token);

        System.out.println("!!!!!@@@@@@@@@@@@@@@@@@@@@");
        System.out.println(loginId);
        System.out.println("!!!!!@@@@@@@@@@@@@@@@@@@@@");
				
		//userEntity를 생성하여 값 set
        User userEntity = new User();
        userEntity.setLoginId(loginId);
        
        // 비번은 토큰에 담겨있지 않았지만, 매번 db에서 조회할 때마다 비번까지 같이 조회하는 것을 막기 위해 아무거나 입력해서 초기화함
        userEntity.setLoginPw("temppassword");
				
		//UserDetails에 회원 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

		//스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
		
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}