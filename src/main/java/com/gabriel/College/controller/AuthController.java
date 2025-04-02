package com.gabriel.College.controller;

import com.gabriel.College.dto.request.StudentRequestDto;
import com.gabriel.College.dto.response.AuthResponseDto;
import com.gabriel.College.service.AuthService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register/student")
	@Transactional
	public ResponseEntity<Void> registerStudent(@Valid @RequestBody StudentRequestDto studentRequestDto) {
		AuthResponseDto response = authService.registerStudent(studentRequestDto);
		return ResponseEntity.ok().build();
	}
}