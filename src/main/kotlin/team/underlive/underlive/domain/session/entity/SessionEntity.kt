package team.underlive.underlive.domain.session.entity

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import team.underlive.underlive.domain.room.entity.RoomEntity
import java.util.UUID

@Entity
@Table(name = "session")
class SessionEntity (
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long?,

	val socket: UUID,
)