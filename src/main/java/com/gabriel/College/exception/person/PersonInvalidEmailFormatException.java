package com.gabriel.College.exception.person;

import com.gabriel.College.exception.ValidationException;
import org.apache.commons.validator.routines.EmailValidator;

public class PersonInvalidEmailFormatException extends ValidationException {
	public PersonInvalidEmailFormatException(String email){
	super(String.format("Invalid email format: %s. %s", email, determineReason(email)),
			"Please provide a valid email address");
}

private static String determineReason(String email) {
	EmailValidator validator = EmailValidator.getInstance();

	if (email == null || email.isEmpty()) {
		return "Email cannot be empty";
	}

	if (!email.contains("@")) {
		return "Email must contain @ symbol";
	}

	String[] parts = email.split("@");
	String localPart = parts[0];
	String domainPart = parts.length > 1 ? parts[1] : "";

	if (localPart.isEmpty()) {
		return "Personname part cannot be empty";
	}

	if (localPart.startsWith(".")) {
		return "Personname cannot start with a dot";
	}

	if (localPart.endsWith(".")) {
		return "Personname cannot end with a dot";
	}

	if (domainPart.isEmpty()) {
		return "Domain part cannot be empty";
	}

	if (domainPart.startsWith(".")) {
		return "Domain cannot start with a dot";
	}

	if (domainPart.endsWith(".")) {
		return "Domain cannot end with a dot";
	}

	if (!domainPart.contains(".")) {
		return "Domain must include at least one dot";
	}

	// If we got here but the validator still says it's invalid,
	// there's some other RFC rule being broken
	if (!validator.isValid(email)) {
		return "Email doesn't comply with RFC 5322 standards";
	}

	return "Unknown validation error";
}
}
