package com.gabriel.College.controller;

import com.gabriel.College.dto.request.PasswordSetupDto;
import com.gabriel.College.dto.request.RegistrationDto;
import com.gabriel.College.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/register")
public class RegistrationController {

	private final RegistrationService registrationService;

	public RegistrationController(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}

	@PostMapping("")
	public ResponseEntity<String> registerStudent(@Valid @RequestBody RegistrationDto registrationDto) {
		registrationService.register(registrationDto);
		return ResponseEntity.ok("Verify email by the link sent on your email address");

	}

	@GetMapping("/confirm-account")
	public ResponseEntity<String> confirmUserAccount(@RequestParam("token")String verificationToken) {
		registrationService.confirmAccount(verificationToken);
		return ResponseEntity.ok("Account activated with success!");
	}

	@PatchMapping("/password")
	public ResponseEntity<String> setupPassword(@Valid @RequestBody PasswordSetupDto passwordSetupDto) {
		registrationService.setupPassword(passwordSetupDto);
		return ResponseEntity.ok("Password created with success");
	}
}
