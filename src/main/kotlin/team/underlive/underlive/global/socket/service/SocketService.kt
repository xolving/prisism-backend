package team.underlive.underlive.global.socket.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import team.underlive.underlive.domain.room.entity.RoomEntity
import team.underlive.underlive.domain.room.repository.RoomRepository
import team.underlive.underlive.global.socket.dto.ChatMessage

@Service
class SocketService(
	private val roomRepository: RoomRepository,
	private val objectMapper: ObjectMapper
){
	val sessions = arrayListOf<WebSocketSession>()

	fun sendMessage(session: WebSocketSession, message: ChatMessage) {
		session.sendMessage(TextMessage(objectMapper.writeValueAsString(message)))
	}

	@Transactional
	fun handlerActions(session: WebSocketSession, chatMessage: ChatMessage) {
		val room = roomRepository.findBySessionAOrSessionB(session.id, session.id)

		if(room.get().sessionA != session.id) {
			sessions.find { it.id == room.get().sessionA}?.let { sendMessage(it, chatMessage) }
		} else if(room.get().sessionB != session.id) {
			sessions.find { it.id == room.get().sessionB}?.let { sendMessage(it, chatMessage) }
		}
	}

	fun establishedConnection(session: WebSocketSession){
		session.sendMessage(TextMessage("{\"status\":\"상대방의 접속을 기다리고 있습니다.\"}"))

		val rooms = roomRepository.findBySessionBIsNull()
		if(rooms.isNotEmpty()){
			roomRepository.save(rooms.get(0).copy(sessionB = session.id))
		}

		if(rooms.isEmpty()){
			roomRepository.save(RoomEntity(
				id = null,
				sessionA = session.id,
				sessionB = null
			))
		}

		sessions.add(session)

		val room = roomRepository.findBySessionAOrSessionB(session.id, session.id)
		if(room.get().sessionA != null && room.get().sessionB != null) {
			sessions.find { it.id == room.get().sessionB }
				?.sendMessage(TextMessage("{\"status\":\"상대방이 들어왔습니다.\"}"))
			sessions.find { it.id == room.get().sessionA }
				?.sendMessage(TextMessage("{\"status\":\"상대방이 들어왔습니다.\"}"))
		}
	}

	fun closedConnection(session: WebSocketSession){
		sessions.remove(session)

		val roomEntity = roomRepository.findBySessionAOrSessionB(session.id, session.id)

		if(roomEntity.isPresent) {
			val sessionA = sessions.find { it.id == roomEntity.get().sessionA }
			val sessionB = sessions.find { it.id == roomEntity.get().sessionB }

			roomRepository.delete(roomEntity.get())

			sessionA?.sendMessage(TextMessage("{\"status\":\"채팅이 종료되었습니다.\"}"))
			sessionA?.close()

			sessionB?.sendMessage(TextMessage("{\"status\":\"채팅이 종료되었습니다.\"}"))
			sessionB?.close()
		}
	}
}