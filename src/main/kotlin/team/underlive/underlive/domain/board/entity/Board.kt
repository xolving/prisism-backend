package team.underlive.underlive.domain.board.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
data class Board(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long,
	val title: String,
	val content: String,
	val createdAt: LocalDateTime,
)
