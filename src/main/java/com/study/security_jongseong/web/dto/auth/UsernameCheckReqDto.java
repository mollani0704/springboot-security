package com.study.security_jongseong.web.dto.auth;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsernameCheckReqDto {
	@NotBlank()
	@Size(max=16, min=4)
	private String username;
}
