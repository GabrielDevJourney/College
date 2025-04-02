package com.gabriel.College.utils;

import com.gabriel.College.exception.person.PersonInvalidPasswordException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordValidator {
	private final BCryptPasswordEncoder passwordEncoder;


	private static final Pattern PASSWORD_PATTERN =
			Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");

	public PasswordValidator(BCryptPasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void validatePassword(String password) {
		if (password == null || password.trim().isEmpty()) {
			throw new PersonInvalidPasswordException("Password cannot be empty");
		}

		if (password.length() < 8) {
			throw new PersonInvalidPasswordException("Password must be at least 8 characters long");
		}

		if (!PASSWORD_PATTERN.matcher(password).matches()) {
			throw new PersonInvalidPasswordException(
					"Password must contain at least one digit, one lowercase letter, " +
							"one uppercase letter, and one special character"
			);
		}
	}


	public String encryptPassword(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}

	public boolean matches(String rawPassword, String encodedPassword) {
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}}