package team.underlive.underlive.global.socket.handler

import com.fasterxml.jackson.databind.ObjectMapper
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

@Component
@Transactional
class WebSocketHandler(
	private val objectMapper: ObjectMapper,
	private val roomService: RoomService,
	private val socketService: SocketService,
	private val sessionRepository: SessionRepository,
	private val roomRepository: RoomRepository,
) : TextWebSocketHandler() {
	override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
		val chatMessage: ChatMessage = objectMapper.readValue(message.payload, ChatMessage::class.java)
		socketService.handlerActions(session, chatMessage, roomService)
	}

	override fun afterConnectionEstablished(session: WebSocketSession) {
		session.sendMessage(TextMessage("{\"status\":\"WAIT\"}"))

		val sessionEntity = SessionEntity(null, UUID.fromString(session.id))
		sessionRepository.save(sessionEntity)

		socketService.sessions[UUID.fromString(session.id)] = session

		val rooms = roomRepository.findRoomsWithSingleSession()

		if(rooms.isEmpty()){
			val roomEntity = RoomEntity(null, mutableListOf(sessionEntity))
			roomRepository.save(roomEntity)
			rooms.add(roomEntity)
		} else {
			val roomEntity = rooms[0]
			roomEntity.sessions.add(sessionEntity)
			roomRepository.save(roomEntity)
			rooms.add(roomEntity)
		}

		if(rooms[0].sessions.size == 2){
			socketService.sessions.map { (key, value) -> run {
				val currentSession = sessionRepository.findBySocket(key)
				if(rooms[0].sessions.isNotEmpty() && rooms[0].sessions.contains(currentSession.get())){
					value.sendMessage(TextMessage("{\"status\":\"JOIN\"}"))
				}
			}}
		}

		super.afterConnectionEstablished(session)
	}

	override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
		socketService.sessions.remove(UUID.fromString(session.id))

		val sessionEntity = sessionRepository.findBySocket(UUID.fromString(session.id))

		if(sessionEntity.isPresent){
			val roomEntity = roomRepository.findBySessionsContains(sessionEntity.get())

			socketService.sessions.map { (key, value) -> run {
				val currentSession = sessionRepository.findBySocket(key)
				if(roomEntity.get().sessions.contains(currentSession.get())){
					value.sendMessage(TextMessage("{\"status\":\"EXIT\"}"))
					value.close()
					sessionRepository.delete(currentSession.get())
				}
			}}

			roomRepository.deleteBySessionsContains(sessionEntity.get())
		}

		super.afterConnectionClosed(session, status)
	}
}