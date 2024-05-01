package team.underlive.underlive.domain.room.entity

import jakarta.persistence.*
import team.underlive.underlive.domain.session.entity.SessionEntity

@Entity
@Table(name = "room")
class RoomEntity(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long?,

	@OneToMany
	val sessions: MutableList<SessionEntity>
)