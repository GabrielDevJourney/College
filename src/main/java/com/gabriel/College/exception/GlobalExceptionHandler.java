package com.gabriel.College.exception;

import com.gabriel.College.exception.person.PersonEmailAlreadyExistsException;
import com.gabriel.College.exception.person.PersonIsNotActiveException;
import com.gabriel.College.exception.person.PersonPhoneNumberAlreadyExistsException;
import com.gabriel.College.exception.token.TokenExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	//*PARENT EXCEPTION HANDLING
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
		logger.error("Failed to find resource in database: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body("The requested resource was not found!");
	}

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<String> handleCustomValidation(ValidationException ex) {
		logger.error("Request validation failed with error: {}", ex.getMessage());
		return ResponseEntity.badRequest()
				.body(ex.getMessageForClient());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> handleArgumentValidation(MethodArgumentNotValidException ex) {
		String errorMessage = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.collect(Collectors.joining(", "));

		logger.error("Request field validation failed. Details: {}", errorMessage);
		return ResponseEntity.badRequest()
				.body(errorMessage); // Return the actual error messages
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleUnexpected(Exception ex) {
		logger.error("Unhandled exception occurred. Error: {}, Stack trace: ", ex.getMessage(), ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("An unexpected error occurred. Please try again later.");
	}

	//*CUSTOMIZE EXCEPTION HANDLING
	@ExceptionHandler(PersonEmailAlreadyExistsException.class)
	public ResponseEntity<String> handleEmailExists(PersonEmailAlreadyExistsException ex) {
		logger.error("Email conflict: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(ex.getMessageForClient());
	}

	@ExceptionHandler(PersonPhoneNumberAlreadyExistsException.class)
	public ResponseEntity<String> handlePhoneNumberExists(PersonPhoneNumberAlreadyExistsException ex){
		logger.error("Phone number conflict : {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessageForClient());
	}

	@ExceptionHandler(PersonIsNotActiveException.class)
	public ResponseEntity<String> handlePersonNotActive(PersonIsNotActiveException ex){
		logger.error("Person account not active : {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessageForClient());
	}

	@ExceptionHandler(TokenExpiredException.class)
	public ResponseEntity<String> handleTokenExpired(TokenExpiredException ex){
		logger.error("Token expired : {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.GONE).body(ex.getMessageForClient());
	}


}