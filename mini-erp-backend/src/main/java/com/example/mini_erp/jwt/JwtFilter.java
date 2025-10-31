package com.example.mini_erp.jwt;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        System.out.println("============================================");
        System.out.println("➡️ Request: " + method + " " + path);

        // 放行登入/註冊
        if (path.startsWith("/users/login") || path.startsWith("/users/register")) {
            System.out.println("✅ 放行登入/註冊請求");
            filterChain.doFilter(request, response);
            return;
        }

        // 放行 OPTIONS 預檢請求
        if ("OPTIONS".equalsIgnoreCase(method)) {
            System.out.println("✅ 放行 OPTIONS 請求");
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        System.out.println("🔍 Authorization Header: " + (authHeader != null ? authHeader.substring(0, Math.min(30, authHeader.length())) + "..." : "null"));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ Missing or invalid Authorization header");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"error\": \"Missing or invalid token\"}");
            return;
        }

        String token = authHeader.substring(7);
        System.out.println("🔑 Token 長度: " + token.length());
        System.out.println("🔑 Token 前 30 字元: " + token.substring(0, Math.min(30, token.length())) + "...");

        try {
            // 使用新版方法驗證 Access Token
            String username = JwtUtil.validateAccessToken(token);
            System.out.println("✅ Token 驗證成功! User: " + username);

            // 設置 Spring Security 認證
            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.out.println("❌ Token 已過期: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"error\": \"Access token expired\"}");
        } catch (io.jsonwebtoken.security.SignatureException e) {
            System.out.println("❌ Token 簽名無效: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"error\": \"Access token signature invalid\"}");
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            System.out.println("❌ Token 格式錯誤: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"error\": \"Malformed token\"}");
        } catch (Exception e) {
            System.out.println("❌ Token 驗證失敗 (" + e.getClass().getSimpleName() + "): " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"error\": \"Access token invalid\"}");
        }
    }
}