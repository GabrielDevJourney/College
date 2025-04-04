package com.gabriel.College.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "verification_token")
public class VerificationToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "verification_token", nullable = false, unique = true)
	private String verificationToken;

	@OneToOne(targetEntity = Person.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "person_id", nullable = false)
	private Person person;

	@Temporal(TemporalType.TIMESTAMP)
		private LocalDateTime createdDate;

	private LocalDateTime expiryDate;

	public VerificationToken(Person person) {
		this.person = person;
		createdDate = LocalDateTime.now();
		verificationToken = UUID.randomUUID().toString();
		expiryDate = createdDate.plusMinutes(2);
	}
}
