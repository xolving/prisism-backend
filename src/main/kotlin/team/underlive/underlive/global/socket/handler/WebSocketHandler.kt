package team.underlive.underlive.global.socket.handler

import com.fasterxml.jackson.databind.ObjectMapper
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import team.underlive.underlive.global.socket.dto.ChatMessage
import team.underlive.underlive.domain.room.entity.RoomEntity
import team.underlive.underlive.domain.room.repository.RoomRepository
import team.underlive.underlive.domain.room.service.RoomService
import team.underlive.underlive.domain.session.entity.SessionEntity
import team.underlive.underlive.domain.session.repository.SessionRepository
import team.underlive.underlive.global.socket.service.SocketService
import java.util.*

@Slf4j
@Component
class WebSocketHandler(
	private val objectMapper: ObjectMapper,
	private val roomService: RoomService,
	private val socketService: SocketService,
	private val sessionRepository: SessionRepository,
	private val roomRepository: RoomRepository,
) : TextWebSocketHandler() {
	@Transactional
	override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
		val chatMessage: ChatMessage = objectMapper.readValue(message.payload, ChatMessage::class.java)
		socketService.handlerActions(session, chatMessage, roomService)
	}

	@Transactional
	override fun afterConnectionEstablished(session: WebSocketSession) {
		val sessionEntity = SessionEntity(null, UUID.fromString(session.id))
		sessionRepository.save(sessionEntity)

		socketService.sessions.put(UUID.fromString(session.id), session)


		if(!roomRepository.existsRoomWithSingleSession()){
			roomRepository.save(RoomEntity(
				id = null,
				sessions = mutableListOf()
			))
		}

		val roomEntity = roomRepository.findRoomsWithSingleSession().get()
		roomEntity.sessions.add(sessionEntity)
		roomRepository.save(roomEntity)

		if(roomEntity.sessions.size == 2){
			socketService.sessions.map { (key, value) -> run {
				val currentSession = sessionRepository.findBySocket(key)
				if(roomEntity.sessions.contains(currentSession.get())){
					value.sendMessage(TextMessage("{\"message\": \"상대방과 매칭되었습니다.\"}"))
				}
			}}
		}

		super.afterConnectionEstablished(session)
	}

	@Transactional
	override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
		socketService.sessions.remove(UUID.fromString(session.id))

		val sessionEntity = sessionRepository.findBySocket(UUID.fromString(session.id)).get()
		val roomEntity = roomRepository.findBySessionsContains(sessionEntity).get()

		socketService.sessions.map { (key, value) -> run {
			val currentSession = sessionRepository.findBySocket(key)
			if(roomEntity.sessions.contains(currentSession.get())){
				value.sendMessage(TextMessage("{\"message\": \"상대방이 채팅을 종료하였습니다.\"}"))
			}
		}}

		roomRepository.deleteBySessionsContains(sessionEntity)
		roomEntity.sessions.map { currentSession ->
			sessionRepository.delete(currentSession)
		}

		super.afterConnectionClosed(session, status)
	}
}