package com.gabriel.College.mapper;

import com.gabriel.College.dto.response.AuthResponseDto;
import com.gabriel.College.entity.Person;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AuthMapper {
	AuthResponseDto toAuthResponseDto(Person person);
}
