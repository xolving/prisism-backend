package team.underlive.underlive.domain.board.entity.dto

import java.time.LocalDateTime

data class FetchBoardListResponse(
	val id: Long,
	val title: String,
	val createdAt: LocalDateTime,
)
