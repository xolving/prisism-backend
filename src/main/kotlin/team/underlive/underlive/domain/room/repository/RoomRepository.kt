package team.underlive.underlive.domain.room.repository

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import team.underlive.underlive.domain.room.entity.Room
import java.util.Optional

interface RoomRepository: JpaRepository<Room, Long> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	fun findBySessionAOrSessionB(a: String, b: String): Optional<Room>

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	fun findBySessionBIsNull(): List<Room>
}