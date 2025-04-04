package com.gabriel.College.utils;

import com.gabriel.College.exception.person.PersonEmailAlreadyExistsException;
import com.gabriel.College.exception.person.PersonInvalidDataException;
import com.gabriel.College.exception.person.PersonInvalidNumberException;
import com.gabriel.College.exception.person.PersonPhoneNumberAlreadyExistsException;
import com.gabriel.College.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class RegistrationValidator {
	private final PersonRepository personRepository;
	private final EmailValidator emailValidator;
	private static final Logger log = LoggerFactory.getLogger(RegistrationValidator.class);

	public RegistrationValidator(PersonRepository personRepository, EmailValidator emailValidator) {
		this.personRepository = personRepository;
		this.emailValidator = emailValidator;
	}

	public void validatePersonData(String email, String phoneNumber, LocalDate birthDate) {
		validateEmailUniqueness(email);
		validatePhoneNumberUniqueness(phoneNumber);
		validateBirthdateStudent(birthDate);
	}
	public String normalizeName(String name) {
		String trimmedName = name.trim();

		//no separate at hyphen apostrophes
		String[] parts = trimmedName.split("(?<=[\\s'-])|(?=[\\s'-])");
		//more efficient when it comes to concat strings it can alter same string without the need to create new
		// string in heap
		StringBuilder normalizedName = new StringBuilder();

		boolean capitalizeNextChar = true;

		for (String part : parts) {
			if (part.matches("[\\s'-]")) {
				normalizedName.append(part);
				capitalizeNextChar = true;
			} else if (capitalizeNextChar) {
				if (!part.isEmpty()) {
					normalizedName.append(Character.toUpperCase(part.charAt(0)));
					if (part.length() > 1) {
						normalizedName.append(part.substring(1).toLowerCase());
					}
					capitalizeNextChar = false;
				}
			} else {
				normalizedName.append(part.toLowerCase());
			}
		}
		return normalizedName.toString();
	}

	public String validateEmailFormatAndNormalize(String email) {
		return emailValidator.validateEmailFormatAndNormalize(email);
	}

	private boolean existsByEmail(String email) {
		return personRepository.existsByEmail(email);
	}

	private boolean existsByPhoneNumber(String phoneNumber) {
		return personRepository.existsByPhoneNumber(phoneNumber);
	}

	private void validateEmailUniqueness(String email) {
		if (existsByEmail(email)) {
			log.error("Person registration failed: Email {} already exists", email);
			throw new PersonEmailAlreadyExistsException(email);
		}
	}

	public String validatePhoneNumberFormat(String phoneNumber) {
		if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
			throw new PersonInvalidDataException("phoneNumber", "Phone number cannot be empty");
		}

		if (!phoneNumber.matches("^(91|92|93|96)\\d{7}$")) {
			throw new PersonInvalidNumberException(phoneNumber);
		}

		return phoneNumber;
	}

	private void validatePhoneNumberUniqueness(String phoneNumber) {
		if (existsByPhoneNumber(phoneNumber)) {
			throw new PersonPhoneNumberAlreadyExistsException(phoneNumber);
		}
	}

	private void validateBirthdateStudent(LocalDate birthDate) {
		if (birthDate == null) {
			throw new PersonInvalidDataException("birthDate", "Birthdate cannot be null");
		}
		int age = LocalDate.now().getYear() - birthDate.getYear();
		if ((birthDate.isAfter(LocalDate.now().minusYears(age))) || (birthDate.isAfter(LocalDate.now()))) {
			age--;
		}
		if (age < 18 || age > 100) {
			throw new PersonInvalidDataException("birthDate", "Age must be between 18 and 100 years");
		}
	}
}
