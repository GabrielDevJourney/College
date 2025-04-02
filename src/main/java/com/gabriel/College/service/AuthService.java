package com.gabriel.College.service;

import com.gabriel.College.dto.request.StudentRequestDto;
import com.gabriel.College.dto.response.AuthResponseDto;
import com.gabriel.College.entity.Person;
import com.gabriel.College.entity.Student;
import com.gabriel.College.enums.RoleType;
import com.gabriel.College.exception.person.PersonEmailAlreadyExistsException;
import com.gabriel.College.exception.person.PersonInvalidDataException;
import com.gabriel.College.exception.person.PersonInvalidNumberException;
import com.gabriel.College.exception.person.PersonPhoneNumberAlreadyExistsException;
import com.gabriel.College.mapper.AuthMapper;
import com.gabriel.College.mapper.PersonMapper;
import com.gabriel.College.repository.PersonRepository;
import com.gabriel.College.repository.StudentRepository;
import com.gabriel.College.security.JwtTokenProvider;
import com.gabriel.College.utils.EmailValidator;
import com.gabriel.College.utils.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AuthService {
	private final PersonRepository personRepository;
	private final StudentRepository studentRepository;
	private final PersonMapper personMapper;
	private final AuthMapper authMapper;
	private final PasswordValidator passwordValidation;
	private final EmailValidator emailValidation;
	private final JwtTokenProvider jwtTokenUtil;
	private static final Logger log = LoggerFactory.getLogger(AuthService.class);

	public AuthService(PersonRepository personRepository, StudentRepository studentRepository, PersonMapper personMapper, AuthMapper authMapper,
	                   PasswordValidator passwordValidation, EmailValidator emailValidation, JwtTokenProvider jwtTokenUtil) {
		this.personRepository = personRepository;
		this.studentRepository = studentRepository;
		this.personMapper = personMapper;
		this.authMapper = authMapper;
		this.passwordValidation = passwordValidation;
		this.emailValidation = emailValidation;
		this.jwtTokenUtil = jwtTokenUtil;
	}

	//LOGIN
	//for this i will need to use generic loginequestDto but when going to database check if person exists bring
	// its role so when token is genereated it can be properly created since both student and teacher use the same
	//request dto this needs to be properly setup to be agnostic
	//1 - loginrequestdto
	//2 - filter email with role for token generation i user table

	public AuthResponseDto registerStudent(StudentRequestDto studentRequestDto){
		String firstName = normalizeName(studentRequestDto.getFirstName());
		String lastName = normalizeName(studentRequestDto.getLastName());
		String personEmail = validateEmailFormatAndNormalize(studentRequestDto.getEmail());
		String phoneNumber = validatePhoneNumberFormat(studentRequestDto.getPhoneNumber());
		LocalDate brithDate = studentRequestDto.getBirthDate();
		String password = studentRequestDto.getPassword();

		validateEmailUniqueness(personEmail);
		validatePhoneNumberUniqueness(phoneNumber);
		validateBirthdateStudent(brithDate);
		passwordValidation.validatePassword(password);

		String encryptedPassword = passwordValidation.encryptPassword(password);
		Person person = new Person();
		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setEmail(personEmail);
		person.setPhoneNumber(phoneNumber);
		person.setBirthDate(brithDate);
		person.setPassword(encryptedPassword);
		person.setRole(RoleType.STUDENT);

		//create student for based on the new person created
		Student student = new Student();
		student.setId(person.getId());
		student.setPerson(person);

		studentRepository.save(student);
		personRepository.save(person);

		return authMapper.toAuthResponseDto(person);
	}


/*	public AuthResponseDto registerTeacher(TeacherRequestDto teacherRequestDto){

	}

	public AuthResponseDto login (LoginRequestDto loginRequestDto){

	}*/

	//* Private helper methods
	private boolean existsByEmail(String email){
		return personRepository.existsByEmail(email);
	}

	private boolean existsByPhoneNumber(String phoneNumber){
		return  personRepository.existsByPhoneNumber(phoneNumber);
	}
	private String validateEmailFormatAndNormalize(String email){
		return emailValidation.validateEmailFormatAndNormalize(email);
	}

	private void validateEmailUniqueness(String email) {
		if (existsByEmail(email)) {
			log.error("Person registration failed: Email {} already exists", email);
			throw new PersonEmailAlreadyExistsException(email);
		}
	}

	private String normalizeName(String name){
		String trimmedName = name.trim();

		//no separate at hyphen apostrophes
		String[] parts = trimmedName.split("(?<=[\\s'-])|(?=[\\s'-])");
		//more efficient when it comes to concat strings it can alter same string without the need to create new
		// string in heap
		StringBuilder normalizedName = new StringBuilder();

		boolean capitalizeNextChar = true;

		for(String part : parts){
			if(part.matches("[\\s'-]")){
				normalizedName.append(part);
				capitalizeNextChar = true;
			}else if(capitalizeNextChar){
				if(!part.isEmpty()){
					normalizedName.append(Character.toUpperCase(part.charAt(0)));
					if(part.length() > 1){
						normalizedName.append(part.substring(1).toLowerCase());
					}
					capitalizeNextChar = false;
				}
			}else{
				normalizedName.append(part.toLowerCase());
			}
		}
		return normalizedName.toString();
	}

	private String validatePhoneNumberFormat(String phoneNumber) {
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
