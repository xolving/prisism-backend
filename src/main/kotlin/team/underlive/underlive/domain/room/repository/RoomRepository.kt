package team.underlive.underlive.domain.room.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.underlive.underlive.domain.room.entity.Room
import java.util.Optional

interface RoomRepository: JpaRepository<Room, Long> {
	fun findBySessionAOrSessionB(a: String, b: String): Optional<Room>

	fun findBySessionBIsNull(): List<Room>
}