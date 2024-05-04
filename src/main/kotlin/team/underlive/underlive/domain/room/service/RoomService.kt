package team.underlive.underlive.domain.room.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import team.underlive.underlive.global.socket.dto.ChatMessage
import team.underlive.underlive.domain.room.controller.dto.res.PlayerCountResponse
import team.underlive.underlive.domain.room.repository.RoomRepository
import team.underlive.underlive.domain.session.repository.SessionRepository
import team.underlive.underlive.global.socket.service.SocketService
import java.util.*
import java.util.concurrent.ConcurrentHashMap

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