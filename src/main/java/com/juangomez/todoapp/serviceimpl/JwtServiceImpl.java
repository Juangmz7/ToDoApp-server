package com.juangomez.todoapp.serviceimpl;

import com.juangomez.todoapp.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class JwtServiceImpl implements JwtService {

    // Secret and encoded key
    private final String secretKey;

    public JwtServiceImpl() { secretKey = generateSecretKey(); }

    /**
     * Returns the private key value
     */
    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            SecretKey key = keyGenerator.generateKey();
            System.out.println("Secret key generated: " + key );
            return Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .issuedAt(new Date(System.currentTimeMillis()))
                // 30 minutes for token expiration
                .expiration( new Date(System.currentTimeMillis() + 1000*60*30 ) )
                .compact();
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        String tokenUsername = extractUsername(token);
        return userDetails.getUsername().equals(tokenUsername) && isNotExpired(token);
    }

    /**
     * Extracts from the token the claim indicated in claimResolver
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    /**
     *  Takes all token claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getKey())
                .build()
                .parseSignedClaims(token)  // Verify the signature of the token
                .getPayload();
    }

    // Verifies token expiration
    private boolean isNotExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date(System.currentTimeMillis()));
    }
}
