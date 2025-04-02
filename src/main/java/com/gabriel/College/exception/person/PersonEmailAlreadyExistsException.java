package com.gabriel.College.exception.person;

import com.gabriel.College.exception.ValidationException;

public class PersonEmailAlreadyExistsException extends ValidationException {
	public PersonEmailAlreadyExistsException(String email) {
		super(String.format("Email already exists: %s ", email),"Invalid email!");
	}
}
