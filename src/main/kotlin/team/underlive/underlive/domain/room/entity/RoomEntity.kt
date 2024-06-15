package team.underlive.underlive.domain.room.entity

import jakarta.persistence.*

@Entity
@Table(name = "room")
data class RoomEntity(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long?,

	val sessionA: String?,

	val sessionB: String?
)