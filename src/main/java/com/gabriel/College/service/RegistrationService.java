package com.gabriel.College.service;

import com.gabriel.College.dto.request.PasswordSetupDto;
import com.gabriel.College.dto.request.RegistrationDto;
import com.gabriel.College.entity.Person;
import com.gabriel.College.entity.Student;
import com.gabriel.College.entity.Teacher;
import com.gabriel.College.entity.VerificationToken;
import com.gabriel.College.enums.RoleType;
import com.gabriel.College.exception.person.PersonInvalidDataException;
import com.gabriel.College.exception.person.PersonIsNotActiveException;
import com.gabriel.College.exception.person.PersonNotFoundException;
import com.gabriel.College.exception.token.TokenExpiredException;
import com.gabriel.College.exception.token.TokenInvalidException;
import com.gabriel.College.repository.PersonRepository;
import com.gabriel.College.repository.StudentRepository;
import com.gabriel.College.repository.TeacherRepository;
import com.gabriel.College.repository.VerificationTokenRepository;
import com.gabriel.College.utils.PasswordValidator;
import com.gabriel.College.utils.RegistrationValidator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class RegistrationService {
	private final PersonRepository personRepository;
	private final StudentRepository studentRepository;
	private final TeacherRepository teacherRepository;
	private final VerificationTokenRepository verificationTokenRepository;
	private final EmailService emailService;
	private final RegistrationValidator registrationValidator;
	private final PasswordValidator passwordValidator;
	private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);

	public RegistrationService(PersonRepository personRepository, StudentRepository studentRepository, TeacherRepository teacherRepository, VerificationTokenRepository verificationTokenRepository, EmailService emailService, RegistrationValidator registrationValidator, PasswordValidator passwordValidator) {
		this.personRepository = personRepository;
		this.studentRepository = studentRepository;
		this.teacherRepository = teacherRepository;
		this.verificationTokenRepository = verificationTokenRepository;
		this.emailService = emailService;
		this.registrationValidator = registrationValidator;
		this.passwordValidator = passwordValidator;
	}


	@Transactional
	public void register(RegistrationDto registrationDto) {
		Person person = createPersonWithoutPassword(registrationDto);

		Person personSaved = personRepository.saveAndFlush(person);
		saveBasedOnRole(personSaved);

		VerificationToken verificationToken = new VerificationToken(personSaved);
		verificationTokenRepository.save(verificationToken);
		String email = personSaved.getEmail();

		emailService.sendVerificationEmail(email,verificationToken);
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

	//* PRIVATE HELPER METHODS
	private Person createPersonWithoutPassword(RegistrationDto registrationDto) {
		String firstName = registrationValidator.normalizeName(registrationDto.getFirstName());
		String lastName = registrationValidator.normalizeName(registrationDto.getLastName());
		String email = registrationValidator.validateEmailFormatAndNormalize(registrationDto.getEmail());
		String phoneNumber = registrationValidator.validatePhoneNumberFormat(registrationDto.getPhoneNumber());
		LocalDate brithDate = registrationDto.getBirthDate();
		RoleType role = registrationDto.getRole();

		registrationValidator.validatePersonData(email, phoneNumber, brithDate);

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
