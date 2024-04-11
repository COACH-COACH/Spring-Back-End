package com.example.demo.jwt;

import java.util.Enumeration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.model.dto.CustomUserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
  	private final JWTUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

		//클라이언트 요청에서 username, password 추출
//        String loginId = obtainUsername(request);
//        String loginPw = obtainPassword(request); // 해당 함수가 뭔지 모르겠지만, 이걸로 우리의 다른 변수 못가져옴
    	
//    	 궁금한 점 : request로 들어오는 모든 파라미터 어떻게 확인함? 아래와 같이 확인 가능

//    	Enumeration<String> parameterNames = request.getParameterNames();
//    	while (parameterNames.hasMoreElements()) {
//    	    String paramName = parameterNames.nextElement();
//    	    String paramValue = request.getParameter(paramName);
//    	    System.out.println(paramName + " : " + paramValue);
//    	}
    	
        String loginId = request.getParameter("loginId");
        String loginPw = request.getParameter("loginPw");
        
//        System.out.println("hwfdoebfirbqovybqo12893798");
//        System.out.println(loginId);
//        System.out.println(loginPw);

				//스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginId, loginPw, null);

				//token에 담은 검증을 위한 AuthenticationManager로 전달
        return authenticationManager.authenticate(authToken);
    }

		//로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
    	System.out.println();
    	System.out.println("successssssssssssssssssssssssssssssssssss");
    	System.out.println();
    	
    	//UserDetailsS
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String loginId = customUserDetails.getUsername(); // userEntity.getLoginId();가 실제로 수행됨

        // 아래 4줄은 role 값을 뽑아내는 방법.. 우린 role 안쓰니까 패스
//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
//        GrantedAuthority auth = iterator.next();
//        String role = auth.getAuthority();

//        String token = jwtUtil.createJwt(loginId, 60*60*10L); // 뒷 놈은 jwt가 살아있을 시간  1000 * 60 * 60 * 24 * 30 이건 30일
        String token = jwtUtil.createJwt(loginId, 1000 * 60L);
        
        // key가 Authorization, Bearer는 인증 방식
        response.addHeader("Authorization", "Bearer " + token); 
    	
    }

		//로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
    	System.out.println();
    	System.out.println("faileddddddddddddddddddddddddddddddddddddddd");
    	System.out.println();
    	
    	response.setStatus(401);
    }
}