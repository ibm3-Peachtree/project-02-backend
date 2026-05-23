package com.ruttu.project_02_backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@Getter
public class JwtUtil {
    @Value("${jwt.secretKey}")
    private String secretKey;
    private final long expirationMs = 1000 * 60 * 60; //토큰 만료 시간 (1시간)
    private final long refreshExpirationMs = 1000 * 60 * 60 * 24 * 14; //14일
    //토큰 생성
    public String generateAccessToken(Long userId, String role){
        System.out.println("jwt secretKey : " + secretKey);
        System.out.println("secretKey length: " + secretKey.length());
        System.out.println("secretKey: [" + secretKey + "]");
        Claims claims = Jwts.claims(); //토큰 안에 넣을 데이터 공간 만듬
        claims.put("userId", userId);
        claims.put("role", role);
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }
    public String generateRefreshToken(Long userId) {

        Claims claims = Jwts.claims();

        claims.put("userId", userId);
        claims.put("type", "refresh");

        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + refreshExpirationMs)
                )
                .signWith(key)
                .compact();
    }
    //JWT 토큰 검증
    public boolean validateToken(String token){
        try{
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            System.out.println("✅ 토큰 검증 성공");
            return true;
        } catch (Exception e) {
            System.out.println("❌ 토큰 검증 실패");
            System.out.println("에러 타입: " + e.getClass().getSimpleName());
            System.out.println("메시지: " + e.getMessage());
            return false;
        }
    }
    //검증 + 내용 꺼내기
    private Claims getClaims(String token){
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();//getBody: payload 꺼내기
    }//토큰에서 email 값을 꺼내기
    public Long getUserIdFromToken(String token){
        Claims claims = getClaims(token); //Claims 꺼내기
        System.out.println("userId : "+claims.get("userId",Long.class));
        return claims.get("userId", Long.class);
    }
    public String getRoleFromToken(String token){
        Claims claims = getClaims(token);

        return claims.get("role", String.class);
    }

}
