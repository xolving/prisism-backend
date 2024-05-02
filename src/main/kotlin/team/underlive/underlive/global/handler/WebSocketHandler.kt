package team.underlive.underlive.global.handler

import com.fasterxml.jackson.databind.ObjectMapper
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import team.underlive.underlive.domain.room.dto.ChatMessage
import team.underlive.underlive.domain.room.entity.RoomEntity
import team.underlive.underlive.domain.room.repository.RoomRepository
import team.underlive.underlive.domain.room.service.RoomService
import team.underlive.underlive.domain.session.entity.SessionEntity
import team.underlive.underlive.domain.session.repository.SessionRepository
import java.util.*

@Slf4j
@Component
class WebSocketHandler(
	private val objectMapper: ObjectMapper,
	private val roomService: RoomService,
	private val sessionRepository: SessionRepository,
	private val roomRepository: RoomRepository,
) : TextWebSocketHandler() {
	@Transactional
	override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
		val chatMessage: ChatMessage = objectMapper.readValue(message.payload, ChatMessage::class.java)
		roomService.handlerActions(session, chatMessage, roomService)
	}

	@Transactional
	override fun afterConnectionEstablished(session: WebSocketSession) {
		val sessionEntity = SessionEntity(null, UUID.fromString(session.id))
		sessionRepository.save(sessionEntity)

		roomService.sessions.put(UUID.fromString(session.id), session)

		session.sendMessage(TextMessage(objectMapper.writeValueAsString("접속에 성공하였습니다.")))

		if(!roomRepository.existsRoomWithSingleSession()){
			roomRepository.save(RoomEntity(
				id = null,
				sessions = mutableListOf()
			))
		}

		val roomEntity = roomRepository.findRoomsWithSingleSession().get()
		roomEntity.sessions.add(sessionEntity)
		roomRepository.save(roomEntity)

		super.afterConnectionEstablished(session)
	}

	@Transactional
	override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
		roomService.sessions.remove(UUID.fromString(session.id))

		val sessionEntity = sessionRepository.findBySocket(UUID.fromString(session.id)).get()
		val roomEntity = roomRepository.findBySessionsContains(sessionEntity).get()

		roomRepository.deleteBySessionsContains(sessionEntity)
		roomEntity.sessions.map { currentSession ->
			sessionRepository.delete(currentSession)
		}

		super.afterConnectionClosed(session, status)
	}
}