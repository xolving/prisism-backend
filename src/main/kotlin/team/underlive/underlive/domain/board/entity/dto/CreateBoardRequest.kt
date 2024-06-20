package team.underlive.underlive.domain.board.entity.dto

import com.fasterxml.jackson.annotation.ObjectIdGenerators.StringIdGenerator
import jakarta.validation.constraints.NotBlank

data class CreateBoardRequest (
	@NotBlank
	val title: String,

	@NotBlank
	val content: String
)