package com.gabriel.College.service;

import com.gabriel.College.mapper.AuthMapper;
import com.gabriel.College.mapper.PersonMapper;
import com.gabriel.College.repository.PersonRepository;
import com.gabriel.College.repository.StudentRepository;
import com.gabriel.College.repository.TeacherRepository;
import com.gabriel.College.repository.VerificationTokenRepository;
import com.gabriel.College.security.JwtTokenProvider;
import com.gabriel.College.utils.PasswordValidator;
import com.gabriel.College.utils.RegistrationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

	private final PersonRepository personRepository;
	private final JwtTokenProvider jwtTokenUtil;

	private static final Logger log = LoggerFactory.getLogger(AuthService.class);

	public AuthService(PersonRepository personRepository, StudentRepository studentRepository, TeacherRepository teacherRepository, VerificationTokenRepository verificationTokenRepository, PersonMapper personMapper, AuthMapper authMapper, EmailService emailService,
	                   PasswordValidator passwordValidator, RegistrationValidator registrationValidator, JwtTokenProvider jwtTokenUtil) {
		this.personRepository = personRepository;
		this.jwtTokenUtil = jwtTokenUtil;
	}
	
	//todo LOGIN verify email, see active status, then check password matches db with decode
	//response with email and token using jwt filter etc
	
}
