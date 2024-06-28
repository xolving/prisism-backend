package team.underlive.underlive.domain.auth.entity.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class UserJoinRequest(
	@field:Email
	@field:NotNull
	val email: String,
	@field:Pattern(
		regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?])(?=.*[a-z]).{8,64}\$",
		message = "비밀번호는 8 ~ 64자리여야 하며, 최소 1개의 대문자와 특수문자, 숫자를 포함해야 합니다.",
	)
	@field:NotNull
	val password: String,
)
