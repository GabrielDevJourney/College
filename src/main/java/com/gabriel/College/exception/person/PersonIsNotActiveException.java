package com.gabriel.College.exception.person;

import com.gabriel.College.exception.ValidationException;

public class PersonIsNotActiveException extends ValidationException {
	public PersonIsNotActiveException(String email) {
		super(String.format("%s is not active ",email), "Please activate account first");
	}
}
