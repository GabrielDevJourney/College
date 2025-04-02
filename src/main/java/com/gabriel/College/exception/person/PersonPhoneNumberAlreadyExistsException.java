package com.gabriel.College.exception.person;

import com.gabriel.College.exception.ValidationException;

public class PersonPhoneNumberAlreadyExistsException extends ValidationException {
	public PersonPhoneNumberAlreadyExistsException(String phoneNumber) {
		super(String.format("Trying to create Person with existent phoneNumber %s",phoneNumber),"Invalid phone number!");
	}
}
