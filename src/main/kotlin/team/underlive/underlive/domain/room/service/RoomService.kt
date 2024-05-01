package team.underlive.underlive.domain.room.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
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
	private val sessions = HashSet<WebSocketSession>()

	fun findAllRoom(): Int {
		val rooms = roomRepository.findAll()
		return rooms.size
	}

	fun <T> sendMessage(session: WebSocketSession, message: T) {
		session.sendMessage(TextMessage(objectMapper.writeValueAsString(message)))
	}

	fun handlerActions(session: WebSocketSession, chatMessage: ChatMessage, roomService: RoomService) {
		val sessionEntity = sessionRepository.findBySocket(UUID.fromString(session.id))
		if(sessionEntity.isEmpty) session.close()


		if (chatMessage.messageType == ChatMessage.MessageType.ENTER) {
			sessions.add(session)
			chatMessage.message = chatMessage.senderId + "님이 입장했습니다."
		}

		sessions.parallelStream()
			.forEach { savedSession: WebSocketSession ->
				run {
					roomService.sendMessage(savedSession, chatMessage)
				}
			}
	}
}