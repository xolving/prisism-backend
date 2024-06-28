package team.underlive.underlive.domain.auth.entity.dto.request

import jakarta.validation.constraints.NotNull

data class UserLoginRequest(
	@field:NotNull
	val email: String,
	@field:NotNull
	val password: String,
)
