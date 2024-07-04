package com.jwt.auth.util;

import com.jwt.auth.model.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.*;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtUtils {
    private String secretKey = "SpecialSecretKey";
    private String authPrefix = "Bearer ";
    private String authHeader = "Authorization";
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private JwtParser jwtParser;

    public JwtUtils() throws NoSuchAlgorithmException {
        /**
         * Получаем пару ключей и по отдельности их сохраняем
         * Выводим в лог
         * Шифровать открытым
         * Расшифровывать закрытым
         */
        KeyPair pair = generateKeyPair();
        this.publicKey = pair.getPublic();
        this.privateKey = pair.getPrivate();
        log.info("Public key: {}, \nPrivate key: {}", this.publicKey, this.privateKey);
        this.jwtParser = Jwts.parser().verifyWith(publicKey).build();
    }

    private static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        /**
         * Данный класс генерирует пару ключей
         * размер ключа будет 2048 бит
         */
        KeyPairGenerator pairGenerator = KeyPairGenerator.getInstance("RSA");
        pairGenerator.initialize(2048);
        return pairGenerator.generateKeyPair();
    }

    public String generateToken(UserEntity user){
        /**
         * Создание самого токена с субьектом
         * и с полезной нагрузкой
         * также установка даты истечения срока дейтсивя токена
         */
        Claims claims = Jwts.claims()
                .subject(user.getEmail()).build();
        claims.put("FirstName", user.getFirstName());
        claims.put("LastName", user.getLastName());
        Date creationDate = new Date();
        Date expirationDate = new Date(creationDate.getTime() + 3600 * 1000);
        return Jwts.builder()
                .claims(claims)
                .expiration(expirationDate)
                .signWith(privateKey)
                .compact();
    }

    public Claims parseToken(String token) {
        /**
         * Парсинг полезной нагрузки токена
         */
        return jwtParser
                .parseSignedClaims(token)
                .getPayload();
    }

    public String resolveToken(HttpServletRequest request) {
        /**
         * Достаем токен из заголовка
         * и приводим его к стрингу
         *
         */
        String bearerToken = request.getHeader(authHeader);
        if (bearerToken != null && bearerToken.startsWith(authPrefix)) {
            return bearerToken.substring(authPrefix.length());
        }
        return null;
    }

    public Claims resolveClaims(HttpServletRequest request) {
        /**
         * Парсим токен из запроса
         * проверяем не пустой ли
         */
        try {
            String token = resolveToken(request);
            if (token != null){
                return parseToken(token);
            }
            return null;
        }catch (ExpiredJwtException ex){
            request.setAttribute("expired", ex.getMessage());
            throw ex;
        }catch (Exception ex){
            request.setAttribute("error", ex.getMessage());
            throw ex;
        }
    }

    public boolean validateClaims(Claims claims){
        /**
         *
         */
        try{
            return claims.getExpiration().after(new Date());
        }catch (Exception ex){
            throw ex;
        }
    }

    public String getEmail(Claims claims){
        return claims.getSubject();
    }

    public List<String> getRoles(Claims claims){
        return (List<String>) claims.get("roles");
    }
}
