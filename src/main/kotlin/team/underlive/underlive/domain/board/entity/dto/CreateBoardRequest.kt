package team.underlive.underlive.domain.board.entity.dto

import jakarta.validation.constraints.NotNull

data class CreateBoardRequest(
	@field:NotNull
	val title: String,
	@field:NotNull
	val content: String,
)
