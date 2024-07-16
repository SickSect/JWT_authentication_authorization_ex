package com.jwt.auth.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtCustomFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    /**
     * Переопределили метод фильтра
     * чтобы каждый новый запрос прогонялся через этот фильтр
     * и читал\валидировал входящий токен
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        /**
         *  Переопределили метод для валидации каждого запроса
         *  и токена из этого запроса
         */
        Map <String, Object> errorDetails = new HashMap<>();
        try{
            String token = jwtUtils.resolveToken(request);
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }
            Claims claims = jwtUtils.resolveClaims(request);
            if (claims != null && jwtUtils.validateClaims(claims)){
                String email = claims.getSubject();
                System.out.println("EMAIL IS: " +  email);
                Authentication authentication = new UsernamePasswordAuthenticationToken(email, "", new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }catch (Exception ex){
            errorDetails.put("message", "Auth error");
            errorDetails.put("details", ex.getMessage());
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), errorDetails);
        }
        filterChain.doFilter(request, response);
    }
}
