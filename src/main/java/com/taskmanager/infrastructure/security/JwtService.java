package com.taskmanager.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service for JWT token operations.
 */
@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    /**
     * Extract username from token.
     *
     * @param token the JWT token
     * @return the username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract expiration date from token.
     *
     * @param token the JWT token
     * @return the expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract a specific claim from token.
     *
     * @param token          the JWT token
     * @param claimsResolver the function to extract the claim
     * @return the claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generate token for user.
     *
     * @param user the user
     * @return the JWT token
     */
    public String generateToken(com.taskmanager.domain.model.User user) {
        return generateToken(new HashMap<>(), user);
    }

    /**
     * Generate token with extra claims.
     *
     * @param extraClaims extra claims to add
     * @param user the user
     * @return the JWT token
     */
    public String generateToken(Map<String, Object> extraClaims, com.taskmanager.domain.model.User user) {
        return buildToken(extraClaims, user.getEmail(), accessTokenExpiration);
    }

    /**
     * Generate refresh token.
     *
     * @param user the user
     * @return the refresh token
     */
    public String generateRefreshToken(com.taskmanager.domain.model.User user) {
        return buildToken(new HashMap<>(), user.getEmail(), refreshTokenExpiration);
    }

    /**
     * Build JWT token.
     *
     * @param extraClaims extra claims
     * @param subject     the subject (username/email)
     * @param expiration  the expiration time in milliseconds
     * @return the JWT token
     */
    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        return Jwts.builder()
            .claims(extraClaims)
            .subject(subject)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey())
            .compact();
    }

    /**
     * Check if token is valid.
     *
     * @param token the JWT token
     * @param user  the user
     * @return true if valid
     */
    public boolean isTokenValid(String token, com.taskmanager.domain.model.User user) {
        final String username = extractUsername(token);
        return (username.equals(user.getEmail())) && !isTokenExpired(token);
    }

    /**
     * Check if token is valid for UserDetails.
     *
     * @param token       the JWT token
     * @param userDetails the user details
     * @return true if valid
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Check if token is expired.
     *
     * @param token the JWT token
     * @return true if expired
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extract all claims from token.
     *
     * @param token the JWT token
     * @return the claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    /**
     * Get the signing key.
     *
     * @return the secret key
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

