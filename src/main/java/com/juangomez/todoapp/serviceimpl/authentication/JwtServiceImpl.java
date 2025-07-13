package com.juangomez.todoapp.serviceimpl.authentication;

import com.juangomez.todoapp.service.authentication.JwtService;
import com.juangomez.todoapp.service.authentication.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    private final String secretKey;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    public JwtServiceImpl() { secretKey = generateSecretKey(); }

    /**
     * Returns the private key value
     */
    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            SecretKey key = keyGenerator.generateKey();
            System.out.println("Secret key generated: " + key );
            return Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating secret key: " + e);
        }
    }

    @Override
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        long JWT_EXPIRATION = 1000 * 60 * 30;
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .issuedAt(new Date(System.currentTimeMillis()))
                // 30 minutes for token expiration
                .expiration( new Date(System.currentTimeMillis() + JWT_EXPIRATION) )
                .compact();
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        boolean validUsername = extractUsername(token).equals(userDetails.getUsername());

        // If the token has expired, or it is in the blacklist
        boolean tokenExpired = isTokenExpired(token) || tokenBlacklistService.isTokenBlacklisted(token);

        return validUsername && !tokenExpired;
    }

    @Override
    public String extractAuthToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    @Override
    public Duration tokenTtl(String token) {
        return Duration.between(
                new Date(System.currentTimeMillis()).toInstant(),
                extractExpiration(token).toInstant()
        );
    }

    /**
     * Extracts from the token the claim indicated in claimResolver
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getKey())
                .build()
                .parseSignedClaims(token)  // Verify the signature of the token
                .getPayload();
    }

    // Verifies token expiration
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date(System.currentTimeMillis()));
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
