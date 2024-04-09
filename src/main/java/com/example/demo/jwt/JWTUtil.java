package com.example.demo.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;

// JWTUtil: JWT 토큰의 생성과 검증에 필요한 유틸리티 메소드를 제공
@Component // 스프링부트의 컴포넌트로 등록되서 관리되도록 어노테이션 작성
public class JWTUtil {

    private SecretKey secretKey;

    // application.properties의 secret 값을 가져와 저
    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // JWT 토큰에서 사용자 ID를 추출
    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("loginId", String.class);
    }

    // JWT 토큰의 유효기간이 만료되었는지 검증
    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date(System.currentTimeMillis()));
    }

    // 토큰 생성 메서드
    // 주어진 사용자 ID(loginId)와 만료 시간(expiredMs)을 이용
    public String createJwt(String loginId, Long expiredMs) {
        return Jwts.builder()
                .claim("loginId", loginId) // 특정 키워드에 대한 데이터 넣기 가능
                .issuedAt(new Date(System.currentTimeMillis())) // 발행 시간
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 만기 시간
                .signWith(secretKey) // 얘로 시그니처 만들어서 암호화 진행
                .compact();
    }
    
}