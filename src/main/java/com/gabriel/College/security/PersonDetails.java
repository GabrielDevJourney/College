package com.gabriel.College.security;

import com.gabriel.College.exception.person.PersonNotFoundException;
import com.gabriel.College.repository.PersonRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PersonDetails implements UserDetailsService {
	private final PersonRepository personRepository;

	public PersonDetails(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return personRepository.findPersonByEmail(email).orElseThrow(PersonNotFoundException::new);
	}
}
