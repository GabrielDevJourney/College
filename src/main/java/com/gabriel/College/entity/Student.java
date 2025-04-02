package com.gabriel.College.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "student")
public class Student {

	@Id
	@Column(name = "id")
	private Long id;

	// when creating from user it will use the same id to create student as well
	@OneToOne
	@MapsId
	@JoinColumn(name = "id")
	private Person person;

}
