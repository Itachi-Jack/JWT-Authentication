package com.example.auth.security;

import com.example.auth.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;



    public JwtService(@Value("${SECRET_KEY}") String secret){

        byte[] keyBytes = Decoders.BASE64.decode(secret);

        this.secretKey = Keys.hmacShaKeyFor(keyBytes);

    }


    public String generateAccessToken(User user){
        long now = System.currentTimeMillis();
        long expiration = now + 1000L * 60 * 30;

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("id" , user.getId())
                .issuedAt(new Date(now))
                .expiration(new Date(expiration))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(User user) {
        long now = System.currentTimeMillis();
        long expiration = now + 1000L * 60 * 60 * 24 * 7;

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("id", user.getId())
                .issuedAt(new Date(now))
                .expiration(new Date(expiration))
                .signWith(secretKey)
                .compact();
    }


    public String extractSubject(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();


    }

    public Long extractId(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("id", Long.class);
    }
}
