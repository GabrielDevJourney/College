package com.gabriel.College.exception.token;

import com.gabriel.College.exception.ValidationException;

public class TokenExpiredException extends ValidationException {
	public TokenExpiredException(String token) {
		super(String.format("Trying to verify expired token: %s",token), "Verification token has expired");
	}
}
