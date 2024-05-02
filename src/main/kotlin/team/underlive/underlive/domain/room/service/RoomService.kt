package team.underlive.underlive.domain.room.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import team.underlive.underlive.domain.chat.ChatMessage
import team.underlive.underlive.domain.room.repository.RoomRepository
import team.underlive.underlive.domain.session.repository.SessionRepository
import java.util.*

@Service
class RoomService(
	private val roomRepository: RoomRepository,
	private val sessionRepository: SessionRepository,
	private val objectMapper: ObjectMapper,
) {
	val sessions = HashSet<WebSocketSession>()

	fun findAllRoom(): Int {
		val rooms = roomRepository.findAll()
		return rooms.size
	}

	fun <T> sendMessage(session: WebSocketSession, message: T) {
		session.sendMessage(TextMessage(objectMapper.writeValueAsString(message)))
	}

	@Transactional
	fun handlerActions(session: WebSocketSession, chatMessage: ChatMessage, roomService: RoomService) {
		val sessionEntity = sessionRepository.findBySocket(UUID.fromString(session.id))
		val roomEntity = roomRepository.findBySessionsContains(sessionEntity.get()).get()
		if(sessionEntity.isEmpty) session.close()

		sessions.parallelStream()
			.forEach { savedSession: WebSocketSession ->
				run {
					val savedSessionEntity = sessionRepository.findBySocket(UUID.fromString(savedSession.id)).get()

					if(roomEntity.sessions.contains(savedSessionEntity) &&
						roomEntity.sessions.size == 2 &&
						session.id != savedSession.id){
						roomService.sendMessage(savedSession, chatMessage)
					}
				}
			}
	}
}