package com.autotrader.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

// @Service marks this as a Spring-managed Bean handling specialized business logic
@Service
public class JwtService {

    // Injects the secret key from your application.properties configuration file
    @Value("${jwt.secret}")
    private String secret;

    // Injects the token expiration duration (in milliseconds) from your configuration file
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // Generates a new cryptographic JWT string token using the user's email as the subject identifier
    public String generateToken(String email){
        return Jwts.builder()
                .subject(email) // Set the payload owner identifier (Subject)
                .issuedAt(new Date()) // Record the exact timestamp when this token is born
                // Calculate the exact timestamp when this token should die (Current Time + Expiration Duration)
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey()) // Sign the token using your secure HMAC cryptographic key to prevent tampering
                .compact(); // Compress everything into a single web-safe URL string separated by dots
    }

    // High-level method used by filters to safely pull the user email out of a token
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    // High-level method used to extract the exact date/time this token expires
    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    /*
        Generic claim extractor.
        <T> makes this method a generic wildcard, letting it return any data type (Strings, Dates, etc.).
        Function<Claims, T> accepts a method reference (like Claims::getSubject) outlining *what* to extract.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        // Crack open the entire payload data package
        final Claims claims = extractAllClaims(token);
        // Execute the extraction rule on the payload and return the resulting value type
        return claimsResolver.apply(claims);
    }

    /*
        Returns true if the token belongs to the matching user email and hasn't crossed its expiration time limit.
     */
    public boolean isTokenValid(String token, String email){
        return extractUsername(token).equals(email) && !isTokenExpired(token);
    }

    // Checks if the token's expiration date is sitting behind the current clock time
    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    // Cracks open the encrypted JWT payload map using your cryptographic key.
    // If the token was altered by an attacker, this method will throw an exception right here.
    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Set verification key
                .build()
                .parseSignedClaims(token) // Unpacks the token
                .getPayload(); // Grabs the body context containing the user claims data
    }

    // Decodes your plain-text/Base64 configured secret and transforms it into a secure HMAC cryptographic SecretKey object
    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}