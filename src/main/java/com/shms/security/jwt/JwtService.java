package com.shms.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;


    // ============================================================
    // GENERATE JWT TOKEN
    // ============================================================

    public String generateToken(UserDetails userDetails) {

        return Jwts.builder()

                .subject(userDetails.getUsername())

                .issuedAt(new Date())

                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + jwtExpiration
                        )
                )

                .signWith(getSigningKey())

                .compact();
    }


    // ============================================================
    // EXTRACT USERNAME / EMAIL FROM TOKEN
    // ============================================================

    public String extractUsername(String token) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }


    // ============================================================
    // VALIDATE TOKEN
    // ============================================================

    public boolean isTokenValid(
            String token,
            UserDetails userDetails) {

        String username =
                extractUsername(token);

        return username.equals(
                    userDetails.getUsername()
                )
                &&
                !isTokenExpired(token);
    }


    // ============================================================
    // CHECK TOKEN EXPIRATION
    // ============================================================

    private boolean isTokenExpired(String token) {

        return extractExpiration(token)
                .before(new Date());
    }


    // ============================================================
    // EXTRACT EXPIRATION
    // ============================================================

    private Date extractExpiration(String token) {

        return extractClaim(
                token,
                Claims::getExpiration
        );
    }


    // ============================================================
    // EXTRACT ANY CLAIM
    // ============================================================

    private <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) {

        Claims claims =
                Jwts.parser()

                        .verifyWith(
                                getSigningKey()
                        )

                        .build()

                        .parseSignedClaims(token)

                        .getPayload();

        return claimsResolver.apply(claims);
    }


    // ============================================================
    // CREATE SIGNING KEY
    // ============================================================

    private SecretKey getSigningKey() {

        /*
         * jwt.secret MUST be a valid STANDARD Base64 encoded
         * secret.
         *
         * Do NOT use a Base64URL secret here.
         */

        byte[] keyBytes =
                Decoders.BASE64.decode(secretKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }
}