package com.gabriel.College.exception.token;

import com.gabriel.College.exception.ValidationException;

public class TokenInvalidException extends ValidationException {
	public TokenInvalidException(String token) {
		super(String.format("Invalid token: " + token), "Unable to verify token");
	}
}
