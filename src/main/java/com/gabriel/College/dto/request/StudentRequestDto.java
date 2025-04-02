package com.gabriel.College.dto.request;

import com.gabriel.College.enums.RoleType;

public class StudentRequestDto extends PersonRequestDto {
	public StudentRequestDto(){
		setRole(RoleType.STUDENT);
	}
}
