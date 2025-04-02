package com.gabriel.College.exception;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
	private final String messageForClient;

	public ValidationException(String message, String messageForClient) {
		super(message);
		this.messageForClient = messageForClient;
	}
}
