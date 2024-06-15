package team.underlive.underlive.domain.room.service

import org.springframework.stereotype.Service
import team.underlive.underlive.domain.room.controller.dto.res.PlayerCountResponse
import team.underlive.underlive.domain.room.repository.RoomRepository
import team.underlive.underlive.global.socket.service.SocketService

@Service
class RoomService(
	private val roomRepository: RoomRepository,
	private val socketService: SocketService
) {
	fun findAllRoom(): Int {
		val rooms = roomRepository.findAll()
		return rooms.size
	}

	fun countAllPlayers(): PlayerCountResponse {
		return PlayerCountResponse(socketService.sessions.size)
	}
}