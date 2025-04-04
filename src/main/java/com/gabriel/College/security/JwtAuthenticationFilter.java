package com.gabriel.College.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtTokenProvider jwtTokenProvider;
	private final PersonDetails personDetails;

	public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, PersonDetails personDetails) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.personDetails = personDetails;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String path = request.getRequestURI();
		if("/api/auth/confirm-account".equals(path)){
			filterChain.doFilter(request, response);
			return;
		}

		final String authorizationHeader = request.getHeader("Authorization");

		String email = null;
		String token = null;

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			//remove "Bearer " from the header to get just the token itself
			token = authorizationHeader.substring(7);
			try {
				email = jwtTokenProvider.extractEmail(token);
			} catch (Exception e) {
				logger.error("Invalid JWT token: {}", e);
			}
		}
		if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails personDetails = this.personDetails.loadUserByUsername(email);

			if (jwtTokenProvider.validateToken(token, personDetails.getUsername())) {
				// Extract roles from JWTg
				String role = jwtTokenProvider.extractRole(token);

				//getting the list of authorized roles since doesn't accept just one
				List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(

						personDetails, null, authorities);
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		filterChain.doFilter(request, response);
	}
}
