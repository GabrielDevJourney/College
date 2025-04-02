package com.gabriel.College.exception.person;

import com.gabriel.College.exception.ValidationException;

public class PersonInvalidPasswordException extends ValidationException {
	public PersonInvalidPasswordException(String reason) {
		super(String.format("Invalid password: %s at auth service", reason),
				String.format("Password is invalid: %s", reason));
	}
}
