package team.underlive.underlive.domain.auth.entity.dto.request

import jakarta.validation.constraints.NotNull

class EmailExistsRequest(
	@field:NotNull
	val email: String,
)
