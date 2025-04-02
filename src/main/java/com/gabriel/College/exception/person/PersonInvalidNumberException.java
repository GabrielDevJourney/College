package com.gabriel.College.exception.person;

import com.gabriel.College.exception.ValidationException;

public class PersonInvalidNumberException extends ValidationException {
	public PersonInvalidNumberException(String phoneNumber) {
		super(String.format("Invalid phone number format %s",phoneNumber),"Enter a valid phone number prefix " +
				"91|92|93|96");
	}
}
