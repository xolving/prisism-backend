package team.underlive.underlive.global.socket.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import team.underlive.underlive.domain.room.repository.RoomRepository
import team.underlive.underlive.domain.room.service.RoomService
import team.underlive.underlive.domain.session.repository.SessionRepository
import team.underlive.underlive.global.socket.dto.ChatMessage
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class SocketService(
	private val roomRepository: RoomRepository,
	private val sessionRepository: SessionRepository,
	private val objectMapper: ObjectMapper,
){
	val sessions = ConcurrentHashMap<UUID, WebSocketSession>()

	fun sendMessage(session: WebSocketSession, message: ChatMessage) {
		session.sendMessage(TextMessage(objectMapper.writeValueAsString(message)))
	}

	@Transactional
	fun handlerActions(session: WebSocketSession, chatMessage: ChatMessage, roomService: RoomService) {
		val sessionEntity = sessionRepository.findBySocket(UUID.fromString(session.id))

		if (sessionEntity.isPresent) {
			val roomEntity = roomRepository.findBySessionsContains(sessionEntity.get())

			if(roomEntity.isPresent){
				sessions.forEach { (key, value) ->
					run {
						val savedSessionEntity = sessionRepository.findBySocket(key).get()

						if(roomEntity.get().sessions.contains(savedSessionEntity)
							&& roomEntity.get().sessions.size == 2 && UUID.fromString(session.id) != key){
							sendMessage(value, chatMessage)
						}
					}
				}
			} else {
				sessionRepository.delete(sessionEntity.get())
			}
		}
	}
}