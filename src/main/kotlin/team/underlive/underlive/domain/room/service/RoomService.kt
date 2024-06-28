package team.underlive.underlive.domain.room.service

import org.springframework.stereotype.Service
import team.underlive.underlive.domain.room.controller.dto.res.PlayerCountResponse
import team.underlive.underlive.global.socket.handler.SocketService

@Service
class RoomService(
	private val socketService: SocketService,
) {
	fun countAllPlayers(): PlayerCountResponse {
		return PlayerCountResponse(socketService.sessions.size)
	}
}
