package com.jwt.auth.util;

import com.jwt.auth.model.UserEntity;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.*;

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
        
    }
}
