package team.underlive.underlive.domain.board.entity.dto

import java.time.LocalDateTime

data class FetchBoardResponse(
	val id: Long,
	val title: String,
	val content: String,
	val createdAt: LocalDateTime,
)
