package com.example.mini_erp.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    // ⭐ 確保密鑰至少 256 bits (32 字元以上)
    private static final String SECRET_STRING = "mysecret123mysecret123mysecret123mysecret123";
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    // Access Token 有效時間:1 小時
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60;

    // Refresh Token 有效時間:7 天
    private static final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7;

    /** 生成 Access Token */
    public static String generateAccessToken(String username) {
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
        
        System.out.println("🔑 生成 Access Token:");
        System.out.println("   User: " + username);
        System.out.println("   Token 長度: " + token.length());
        System.out.println("   Token 前 30 字元: " + token.substring(0, Math.min(30, token.length())) + "...");
        System.out.println("   過期時間: " + new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION));
        
        return token;
    }

    /** 生成 Refresh Token */
    public static String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /** 驗證 Access Token */
    public static String validateAccessToken(String token) throws JwtException {
        System.out.println("🔍 驗證 Access Token:");
        System.out.println("   Token 長度: " + token.length());
        System.out.println("   Token 前 30 字元: " + token.substring(0, Math.min(30, token.length())) + "...");
        
        try {
            String username = validateToken(token);
            System.out.println("   ✅ 驗證成功! User: " + username);
            return username;
        } catch (Exception e) {
            System.out.println("   ❌ 驗證失敗: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            throw e;
        }
    }

    /** 驗證 Refresh Token */
    public static String validateRefreshToken(String token) throws JwtException {
        return validateToken(token);
    }

    /** 共用驗證邏輯 */
    private static String validateToken(String token) throws JwtException {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}