package com.gabriel.College.exception.person;

import com.gabriel.College.exception.ValidationException;

public class PersonInvalidDataException extends ValidationException {
	public PersonInvalidDataException(String field, String messageForClient) {
		super(String.format("Invalid account data: %s - %s", field, messageForClient),
				String.format("Please provide valid %s information", field));
	}
}
