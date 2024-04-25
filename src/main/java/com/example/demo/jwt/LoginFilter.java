// 1. 사용자가 로그인을 시도할 때 LoginFilter가 활성화 됨

package com.example.demo.jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.model.dto.CustomUserDetails;
import com.example.demo.repository.UserRepo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// LoginFilter: 사용자 로그인 시도를 처리하는 필터
// UsernamePasswordAuthenticationFilter를 상속받아 구현
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
  	private final JWTUtil jwtUtil;
  	private final UserRepo urepo;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, UserRepo urepo) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.urepo = urepo;
    }

    // 1) 로그인 시도
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 클라이언트로부터 받은 loginId와 loginPw를 이용해
        String loginId = request.getParameter("loginId");
        String loginPw = request.getParameter("loginPw");
        
        // 여기서 ACTIVE_STATUS가 1인지 확인(true)
        boolean isValid = checkUserStatus(loginId);

        // 0이면 비활성 상태임
        if (!isValid) {
            // 조건에 맞지 않는 경우 AuthenticationException 발생
            throw new BadCredentialsException("User status is not valid");
        }
        
        // UsernamePasswordAuthenticationToken 객체 생성: 아직 인증되지 않은 상태의 토큰
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginId, loginPw, null); // 3번째 인자 = roll(우린 없음)

        // AuthenticationManager에게 전달해 인증 시도
        return authenticationManager.authenticate(authToken);
    }

    // service에 함수 구현하면 SecurityConfig와의 순환 오류 발생. 내용은 아래에
    /*Description:

		The dependencies of some of the beans in the application context form a cycle:
		
		┌─────┐
		|  securityConfig defined in file [C:\Users\Admin\git\Spring-Back-End\target\classes\com\example\demo\config\SecurityConfig.class]
		↑     ↓
		|  userService defined in file [C:\Users\Admin\git\Spring-Back-End\target\classes\com\example\demo\service\UserService.class]
		└─────┘
		
		
		Action:
		
		Relying upon circular references is discouraged and they are prohibited by default. 
		Update your application to remove the dependency cycle between beans. As a last resort, 
		it may be possible to break the cycle automatically by setting spring.main.allow-circular-references to true.
	*/
	private boolean checkUserStatus(String loginId) {
		// TODO Auto-generated method stub
		Boolean status = urepo.findByLoginId(loginId).getActiveStatus();
		if (status == true) {
			return true;
		} else {
			return false;
		}
	}

	// 2-1) 인증에 성공한 경우
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
    	
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String loginId = customUserDetails.getUsername();
        
        // loginId를 기반으로 JWT 토큰 생성(토큰의 유효기간 포함)
        String token = jwtUtil.createJwt(loginId, 1000 * 60L * 10 * 100); // 1000분
        
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