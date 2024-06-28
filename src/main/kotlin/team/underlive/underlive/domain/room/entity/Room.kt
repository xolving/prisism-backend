package team.underlive.underlive.domain.room.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class Room(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long?,
	val sessionA: String?,
	val sessionB: String?,
)
