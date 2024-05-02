package team.underlive.underlive.domain.room.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import team.underlive.underlive.global.socket.dto.ChatMessage
import team.underlive.underlive.domain.room.dto.res.PlayerCountResponse
import team.underlive.underlive.domain.room.repository.RoomRepository
import team.underlive.underlive.domain.session.repository.SessionRepository
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class RoomService(
	private val roomRepository: RoomRepository,
	private val sessionRepository: SessionRepository,
	private val objectMapper: ObjectMapper,
) {
	val sessions = ConcurrentHashMap<UUID, WebSocketSession>()

	fun findAllRoom(): Int {
		val rooms = roomRepository.findAll()
		return rooms.size
	}

	fun countAllPlayers(): PlayerCountResponse {
		return PlayerCountResponse(sessions.size)
	}

	fun <T> sendMessage(session: WebSocketSession, message: T) {
		session.sendMessage(TextMessage(objectMapper.writeValueAsString(message)))
	}

	@Transactional
	fun handlerActions(mySession: WebSocketSession, chatMessage: ChatMessage, roomService: RoomService) {
		val sessionEntity = sessionRepository.findBySocket(UUID.fromString(mySession.id))
		val roomEntity = roomRepository.findBySessionsContains(sessionEntity.get()).get()
		if(sessionEntity.isEmpty) mySession.close()

		sessions.forEach { (key, value) ->
				run {
					val savedSessionEntity = sessionRepository.findBySocket(key).get()

					if(roomEntity.sessions.contains(savedSessionEntity) &&
						roomEntity.sessions.size == 2 &&
						UUID.fromString(mySession.id) != key){
						roomService.sendMessage(value, chatMessage)
					}
				}
			}
	}
}