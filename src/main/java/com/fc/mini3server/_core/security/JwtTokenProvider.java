package com.fc.mini3server._core.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fc.mini3server.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    public static final Long EXP = 1000L * 60 * 60 * 48;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";

    @Value("${secret-key}")
    private static String secretKey;

    private final Environment environment;

    @PostConstruct
    private void init() {
        secretKey = environment.getProperty("secret-key");
    }

    public String create(User user) {
        String jwt = JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXP))
                .withClaim("username", user.getName())
                .withClaim("id", user.getId())
                .withClaim("status", user.getStatus().name())
                .withClaim("auth", user.getAuth().name())
                .withClaim("password", user.getPassword())
                .sign(Algorithm.HMAC512(secretKey));
        return TOKEN_PREFIX + jwt;
    }

    public static DecodedJWT verify(String jwt) {
        return JWT.require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(jwt);
    }

}
