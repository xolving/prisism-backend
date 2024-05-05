package team.underlive.underlive.domain.room.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import team.underlive.underlive.domain.room.entity.RoomEntity
import team.underlive.underlive.domain.session.entity.SessionEntity
import java.util.Optional
import java.util.UUID

interface RoomRepository: JpaRepository<RoomEntity, Long> {
	@Query("SELECT r FROM RoomEntity r WHERE SIZE(r.sessions) = 1")
	fun findRoomsWithSingleSession(): ArrayList<RoomEntity>

	@Query("SELECT COUNT(r) > 0 FROM RoomEntity r WHERE SIZE(r.sessions) = 1")
	fun existsRoomWithSingleSession(): Boolean

	fun findBySessionsContains(sessionEntity: SessionEntity): Optional<RoomEntity>
}