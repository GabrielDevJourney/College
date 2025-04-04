package com.gabriel.College.controller;

import com.gabriel.College.dto.auth.RegistrationDto;
import com.gabriel.College.dto.request.PasswordSetupDto;
import com.gabriel.College.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public ResponseEntity<String> registerStudent(@Valid @RequestBody RegistrationDto registrationDto) {
		authService.register(registrationDto);
		return ResponseEntity.ok("Verify email by the link sent on your email address");

	}

	@GetMapping(value="/confirm-account")
	public ResponseEntity<String> confirmUserAccount(@RequestParam("token")String verificationToken) {
		authService.confirmAccount(verificationToken);
		return ResponseEntity.ok("Account activated with success!");
	}

	@PatchMapping("/password")
	public ResponseEntity<String> setupPassword(@Valid @RequestBody PasswordSetupDto passwordSetupDto) {
		authService.setupPassword(passwordSetupDto);
		return ResponseEntity.ok("Password created with success");
	}
}