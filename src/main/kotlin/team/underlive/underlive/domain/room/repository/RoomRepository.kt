package team.underlive.underlive.domain.room.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import team.underlive.underlive.domain.room.entity.RoomEntity
import java.util.Optional

interface RoomRepository: JpaRepository<RoomEntity, Long> {
	fun findBySessionA(uuid: String): Optional<RoomEntity>

	fun findBySessionAOrSessionB(a: String, b: String): Optional<RoomEntity>

	fun findBySessionAIsNull(): List<RoomEntity>

	fun findBySessionBIsNull(): List<RoomEntity>

	fun findBySessionB(uuid: String): Optional<RoomEntity>
}