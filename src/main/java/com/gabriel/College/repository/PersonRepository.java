package com.gabriel.College.repository;

import com.gabriel.College.entity.Person;
import com.gabriel.College.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
	boolean existsByEmail(String email);
	boolean existsByPhoneNumber(String phoneNumber);
	Optional<Person> findPersonByFirstNameAndLastName(String firstName, String LastName);
	Optional<Person> findPersonByEmail(String email);
	List<Person> findPersonByBirthDate(LocalDate birthDate);
	List<Person> findByRole(RoleType roleType);
}
