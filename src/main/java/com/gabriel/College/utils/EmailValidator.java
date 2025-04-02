package com.gabriel.College.utils;

import com.gabriel.College.exception.person.PersonInvalidDataException;
import com.gabriel.College.exception.person.PersonInvalidEmailFormatException;
import org.springframework.stereotype.Component;

@Component
public class EmailValidator {
	public String validateEmailFormatAndNormalize(String email) {
		// null or empty email
		if (email == null || email.trim().isEmpty()) {
			throw new PersonInvalidDataException("email", "Email cannot be empty");
		}

		// Normalize the email
		String normalizedEmail = email.trim().toLowerCase();
		org.apache.commons.validator.routines.EmailValidator emailValidator = org.apache.commons.validator.routines.EmailValidator.getInstance();

		// basic validity using Apache Commons Validator
		if (!emailValidator.isValid(normalizedEmail)) {
			throw new PersonInvalidEmailFormatException(normalizedEmail);
		}

		String[] parts = normalizedEmail.split("@");
		if (parts.length != 2) {
			throw new PersonInvalidEmailFormatException("Email must contain exactly one '@' symbol: " + normalizedEmail);
		}

		String localPart = parts[0];
		String domainPart = parts[1];

		// empty local and domain parts
		if (localPart.isEmpty() || domainPart.isEmpty()) {
			throw new PersonInvalidEmailFormatException("Local and domain parts cannot be empty: " + normalizedEmail);
		}


		//valid first and last char of each local and domain
		char firstCharLocal = localPart.charAt(0);
		char lastCharLocal = localPart.charAt(localPart.length() - 1);
		if (isInvalidCharacter(firstCharLocal) || isInvalidCharacter(lastCharLocal)) {
			throw new PersonInvalidEmailFormatException("Local part cannot start or end with invalid characters: " + normalizedEmail);
		}

		char firstCharDomain = domainPart.charAt(0);
		char lastCharDomain = domainPart.charAt(domainPart.length() - 1);
		if (isInvalidCharacter(firstCharDomain) || isInvalidCharacter(lastCharDomain)) {
			throw new PersonInvalidEmailFormatException("Domain part cannot start or end with invalid characters: " + normalizedEmail);
		}

		// consecutive dots in local part
		if (localPart.contains("..")) {
			throw new PersonInvalidEmailFormatException("Local part cannot contain consecutive dots: " + normalizedEmail);
		}

		// consecutive dots in domain part
		if (domainPart.contains("..")) {
			throw new PersonInvalidEmailFormatException("Domain part cannot contain consecutive dots: " + normalizedEmail);
		}

		return normalizedEmail;
	}

	private boolean isInvalidCharacter(char c) {
		// invalid characters
		return c == '.' || c == '#' || c == '@' || c == ' ' || c == '-' || c == '_';
	}
}
