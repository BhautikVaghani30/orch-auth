/*
 * Copyright 2025 com.core.township
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.town.auth.util;

import com.town.auth.config.TownJwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * A utility class for handling JSON Web Tokens (JWTs), including creation, parsing, and validation.
 * This class is designed with a functional approach, using {@link Optional} to ensure null safety
 * and provide a fluent, robust API for JWT operations.
 */
@Slf4j
@Service
public class TownJwtUtil {

    private final TownJwtConfig townJwtConfig;
    
    /**
     * The signing key derived from the configured secret. It is used to sign and verify tokens.
     * This key is securely generated and managed internally.
     */
    private Key key;

    /**
     * The JWT parser instance, configured with the signing key.
     * Reusing the parser is more efficient than creating a new one for each token.
     */
    private JwtParser jwtParser;

    public TownJwtUtil(TownJwtConfig townJwtConfig) {
        this.townJwtConfig = townJwtConfig;
    }

    /**
     * Initializes the service after dependency injection.
     * It creates the HMAC-SHA signing key and the JWT parser instance from the configured secret.
     */
    @PostConstruct
    public void init() {
        // Use Optional to safely handle the secret and prevent NullPointerException
        this.key = Optional.ofNullable(townJwtConfig.getSecret())
                .map(String::getBytes)
                .map(Keys::hmacShaKeyFor)
                .orElseThrow(() -> new IllegalStateException("JWT secret key is not configured. Please set 'jwt.secret' in properties."));

        this.jwtParser = Jwts.parserBuilder().setSigningKey(this.key).build();
    }

    /**
     * Extracts the username (subject) from the JWT.
     *
     * @param token The JWT string.
     * @return An {@link Optional} containing the username if present, otherwise an empty Optional.
     */
    public Optional<String> extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the JWT.
     *
     * @param token The JWT string.
     * @return An {@link Optional} containing the expiration date if present, otherwise an empty Optional.
     */
    public Optional<Date> extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * A generic method to extract a specific claim from a token using a provided resolver function.
     * This functional approach allows for flexible and type-safe claim extraction.
     *
     * @param token          The JWT string.
     * @param claimsResolver A function that takes a {@link Claims} object and returns the desired claim.
     * @param <T>            The type of the claim to be extracted.
     * @return An {@link Optional} containing the extracted claim, or an empty Optional if extraction fails.
     */
    public <T> Optional<T> extractClaim(String token, Function<Claims, T> claimsResolver) {
        return extractAllClaims(token).map(claimsResolver);
    }

    /**
     * Parses the token and extracts all claims. It safely handles parsing errors.
     *
     * @param token The JWT string.
     * @return An {@link Optional} of the {@link Claims} object if the token is valid and parsable,
     *         otherwise an empty Optional.
     */
    private Optional<Claims> extractAllClaims(String token) {
        try {
            return Optional.of(jwtParser.parseClaimsJws(token).getBody());
        } catch (Exception e) {
            log.error("TownJwtUtil :: extractAllClaims : Failed to parse JWT token. Reason: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Checks if a token is expired.
     *
     * @param token The JWT string.
     * @return {@code true} if the token is expired, {@code false} otherwise.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token)
                .map(expiration -> expiration.before(new Date()))
                .orElse(true);
    }

    /**
     * Creates a new JWT with the given claims and subject.
     *
     * @param claims  A map of claims to include in the token's payload.
     * @param subject The subject of the token (typically the username).
     * @return A compact, URL-safe JWT string.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        final long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + townJwtConfig.getExpirationMs()))
                .signWith(key, SignatureAlgorithm.HS256).compact();
    }

    /**
     * Generates a JWT for a given username and role.
     *
     * @param username The username to be used as the token's subject.
     * @param role The user role to be included as a custom claim.
     * @param sessionId A unique identifier for the user's session.
     * @param userId The unique identifier for the user.
     * @return The generated JWT string.
     */
    public String generateToken(String username, String role, String sessionId, String userId) {
        // Use immutable map and constants for claims
        return createToken(Map.of(
                TownJwtConstants.ROLE_CLAIM, role,
                TownJwtConstants.SESSION_ID_CLAIM, sessionId,
                TownJwtConstants.USER_ID_CLAIM, userId), username);
    }

    /**
     * Validates a JWT against the user details.
     * A token is considered valid if the username matches and the token is not expired.
     *
     * @param token       The JWT string to validate.
     * @param userDetails The {@link UserDetails} object of the user to validate against.
     * @return {@code true} if the token is valid, {@code false} otherwise.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        return extractUsername(token)
                .map(username -> username.equals(userDetails.getUsername()) && !isTokenExpired(token))
                .orElse(false);
    }
}