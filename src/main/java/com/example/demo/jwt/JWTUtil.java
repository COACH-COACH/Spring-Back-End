package com.example.demo.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;

@Component // 스프링부트의 컴포넌트로 등록되서 관리되도록 어어노테이션 작성
public class JWTUtil {

    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {

        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token) {
    	String theThingToReturn = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("loginId", String.class);
    	System.out.println("!!!!!@@@@@@@@@@@@@@@@@@@@@");
        System.out.println(theThingToReturn);
        System.out.println("!!!!!@@@@@@@@@@@@@@@@@@@@@");
        return theThingToReturn;
    }

//    public String getRole(String token) {
//
//        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
//    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date(System.currentTimeMillis()));
    }

    public String createJwt(String loginId, Long expiredMs) { // 토큰이 살아있을 시간을 받음
    	// 토큰 생성 메소드
        return Jwts.builder()
                .claim("loginId", loginId) // 특정 키워드에 대한 데이터 넣기 가능
                .issuedAt(new Date(System.currentTimeMillis())) // 발행 시간
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 만기 시간
                .signWith(secretKey) // 얘로 시그니처 만들어서 암호화 진행
                .compact();
    }
}