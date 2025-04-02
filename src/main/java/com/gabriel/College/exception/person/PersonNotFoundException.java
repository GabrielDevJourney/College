package com.gabriel.College.exception.person;

import com.gabriel.College.exception.ResourceNotFoundException;

public class PersonNotFoundException extends ResourceNotFoundException {
	public PersonNotFoundException() {
		super("Person not found");
	}
}
