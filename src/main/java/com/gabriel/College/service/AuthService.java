package com.gabriel.College.service;

import com.gabriel.College.dto.auth.RegistrationDto;
import com.gabriel.College.dto.request.PasswordSetupDto;
import com.gabriel.College.entity.Person;
import com.gabriel.College.entity.Student;
import com.gabriel.College.entity.Teacher;
import com.gabriel.College.entity.VerificationToken;
import com.gabriel.College.enums.RoleType;
import com.gabriel.College.exception.person.*;
import com.gabriel.College.exception.token.TokenExpiredException;
import com.gabriel.College.exception.token.TokenInvalidException;
import com.gabriel.College.mapper.AuthMapper;
import com.gabriel.College.mapper.PersonMapper;
import com.gabriel.College.repository.PersonRepository;
import com.gabriel.College.repository.StudentRepository;
import com.gabriel.College.repository.TeacherRepository;
import com.gabriel.College.repository.VerificationTokenRepository;
import com.gabriel.College.security.JwtTokenProvider;
import com.gabriel.College.utils.PasswordValidator;
import com.gabriel.College.utils.PersonValidator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AuthService {
	private final PersonRepository personRepository;
	private final StudentRepository studentRepository;
	private final TeacherRepository teacherRepository;
	private final VerificationTokenRepository verificationTokenRepository;
	private final PersonMapper personMapper;
	private final AuthMapper authMapper;
	private final EmailService emailService;
	private final PersonValidator personValidator;
	private final PasswordValidator passwordValidator;
	private final JwtTokenProvider jwtTokenUtil;

	@Value("${app.base-url}")
	private String baseUrl;

	private static final Logger log = LoggerFactory.getLogger(AuthService.class);

	public AuthService(PersonRepository personRepository, StudentRepository studentRepository, TeacherRepository teacherRepository, VerificationTokenRepository verificationTokenRepository, PersonMapper personMapper, AuthMapper authMapper, EmailService emailService,
	                   PasswordValidator passwordValidator, PersonValidator personValidator, JwtTokenProvider jwtTokenUtil) {
		this.personRepository = personRepository;
		this.studentRepository = studentRepository;
		this.teacherRepository = teacherRepository;
		this.verificationTokenRepository = verificationTokenRepository;
		this.personMapper = personMapper;
		this.authMapper = authMapper;
		this.emailService = emailService;
		this.personValidator = personValidator;
		this.passwordValidator = passwordValidator;
		this.jwtTokenUtil = jwtTokenUtil;
	}

	//* BUSINESS LOGIC
	@Transactional
	public void register(RegistrationDto registrationDto) {
		Person person = createPersonWithoutPassword(registrationDto);

		Person personSaved = personRepository.saveAndFlush(person);
		saveBasedOnRole(personSaved);

		sendVerificationEmail(personSaved);
	}

	@Transactional
	public void confirmAccount(String verificationToken) {
		VerificationToken token = verifyToken(verificationToken);
		String email = token.getPerson().getEmail();
		Person person =
				personRepository.findPersonByEmail(email).orElseThrow(PersonNotFoundException::new);
		person.setActive(true);
		personRepository.save(person);
		verificationTokenRepository.delete(token);

	}

	@Transactional
	public void setupPassword(PasswordSetupDto passwordSetupDto) {
		String email = passwordSetupDto.getEmail();
		Person person = personRepository.findPersonByEmail(email).orElseThrow(PersonNotFoundException::new);

		if (!person.getActive()) {
			throw new PersonIsNotActiveException(person.getEmail());
		}
		String password = passwordSetupDto.getPassword();

		passwordValidator.validatePassword(password);
		String encryptedPassword = passwordValidator.encryptPassword(password);

		person.setPassword(encryptedPassword);
		personRepository.save(person);
	}

	//todo LOGIN verify email, see active status, then check password matches db with decode
	//response with email and token using jwt filter etc

	//* PRIVATE HELPER METHODS
	private Person createPersonWithoutPassword(RegistrationDto registrationDto) {
		String firstName = personValidator.normalizeName(registrationDto.getFirstName());
		String lastName = personValidator.normalizeName(registrationDto.getLastName());
		String email = personValidator.validateEmailFormatAndNormalize(registrationDto.getEmail());
		String phoneNumber = personValidator.validatePhoneNumberFormat(registrationDto.getPhoneNumber());
		LocalDate brithDate = registrationDto.getBirthDate();
		RoleType role = registrationDto.getRole();

		personValidator.validatePersonData(email, phoneNumber, brithDate);

		Person person = new Person();
		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setEmail(email);
		person.setPhoneNumber(phoneNumber);
		person.setBirthDate(brithDate);
		person.setActive(false);
		person.setPassword("");
		person.setRole(role);

		return person;
	}

	private void sendVerificationEmail(Person personSaved) {
		VerificationToken verificationToken = new VerificationToken(personSaved);
		verificationTokenRepository.save(verificationToken);

		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(personSaved.getEmail());
		mailMessage.setSubject("Complete Registration!");
		mailMessage.setText("To confirm your account, please click here : "
				+ baseUrl + "/api/auth/confirm-account?token=" + verificationToken.getVerificationToken());
		emailService.sendEmail(mailMessage);

		log.debug("Confirmation Token: {}", verificationToken.getVerificationToken());

	}

	private void saveBasedOnRole(Person person) {
		if (person.getRole() == null) {
			throw new PersonInvalidDataException("role", "Role cannot be null");
		}

		if (RoleType.STUDENT.equals(person.getRole())) {
			saveStudent(person);
		} else if (RoleType.TEACHER.equals(person.getRole())) {
			saveTeacher(person);
		} else {
			throw new PersonInvalidDataException("role", "Unsupported role: " + person.getRole());
		}
	}

	private void saveStudent(Person person) {
		Student student = new Student();
		student.setPerson(person);
		studentRepository.save(student);
	}

	private void saveTeacher(Person person) {
		Teacher teacher = new Teacher();
		teacher.setPerson(person);
		teacherRepository.save(teacher);
	}

	private VerificationToken verifyToken(String verificationToken) {
		VerificationToken token = verificationTokenRepository.findByVerificationToken(verificationToken);

		if (token == null) {
			throw new TokenInvalidException(verificationToken);
		}

		if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
			verificationTokenRepository.delete(token);
			throw new TokenExpiredException(verificationToken);
		}

		return token;
	}
}
