package com.gabriel.College.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtTokenProvider {
	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.expiration}")
	private long jwtExpiration;

	private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

	//generate toke for user
	public String generateToken(String userId, String email, String role) {
		logger.info("Generating token for user: {}", userId);
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", role);
		claims.put("email", email);
		return createToken(claims, userId);
	}

	private String createToken(Map<String, Object> claims, String subject) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpiration);
		Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

		return Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(now)
				.setExpiration(expiryDate)
				.signWith(key, SignatureAlgorithm.HS512)
				.compact();
	}

	public String extractEmail(String token) {
		String email = extractClaim(token, Claims::getSubject);
		logger.debug("Extracted email from token: {}", email);
		return email;
	}

	//Extract roles from token
	public String extractRole(String token) {
		try {
			Claims claims = extractAllClaims(token);
			return claims.get("role",String.class);
		} catch (Exception e) {
			logger.error("Error extracting role from token", e);
			return null;
		}
	}

	// Extract expiration date from token
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}


	// Check if token has expired
	private Boolean isTokenExpired(String token) {
		final Date expiration = extractExpiration(token);
		boolean expired = expiration.before(new Date());
		if (expired) {
			logger.warn("Token has expired: {}", token);
		}
		return expired;
	}

	// Extract claim specific from token
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	// Extract all claims from token
	private Claims extractAllClaims(String token) {
		try {
			Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
			return Jwts.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(token)
					.getBody();
		} catch (Exception e) {
			logger.error("Failed to extract claims from token: {}", token, e);
			throw e;
		}
	}

	public boolean validateToken(String token, String userEmail) {
		final String email = extractEmail(token);
		boolean isValid = email.equals(userEmail) && !isTokenExpired(token);
		logger.info("Token validation for user: {} - Valid: {}", userEmail, isValid);
		return isValid;
	}
}
