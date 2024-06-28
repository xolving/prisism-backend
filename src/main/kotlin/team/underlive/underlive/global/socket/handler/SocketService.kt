package team.underlive.underlive.global.socket.handler

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import team.underlive.underlive.domain.room.entity.Room
import team.underlive.underlive.domain.room.repository.RoomRepository
import team.underlive.underlive.global.socket.dto.ChatMessage

@Service
class SocketService(
	private val roomRepository: RoomRepository,
	private val objectMapper: ObjectMapper,
) {
	val sessions = arrayListOf<WebSocketSession>()

	fun sendMessage(
		session: WebSocketSession,
		chatMessage: ChatMessage,
	) {
		session.sendMessage(TextMessage(objectMapper.writeValueAsString(chatMessage)))
	}

	fun handlerActions(
		session: WebSocketSession,
		chatMessage: ChatMessage,
	) {
		if (!listOf("PING").contains(chatMessage.type)) {
			val room = roomRepository.findBySessionAOrSessionB(session.id, session.id)
			val isAMe = session.id == room.get().sessionA

			if (listOf("MESSAGE", "JOIN", "EXIT", "WAIT").contains(chatMessage.type)) {
				sessions.find { it.id == room.get().sessionA }?.let {
					sendMessage(it, ChatMessage(content = chatMessage.content, sender = if (isAMe) "나" else "상대방", type = "MESSAGE"))
				}
				sessions.find { it.id == room.get().sessionB }?.let {
					sendMessage(
						it,
						ChatMessage(content = chatMessage.content, sender = if (!isAMe) "나" else "상대방", type = "MESSAGE"),
					)
				}
			} else if (listOf("WRITE").contains(chatMessage.type)) {
				sessions.find { it.id == (if (isAMe) room.get().sessionB else room.get().sessionA) }?.let {
					sendMessage(it, ChatMessage(content = chatMessage.content, sender = null, type = "WRITE"))
				}
			}
		}
	}

	fun establishedConnection(session: WebSocketSession) {
		sendMessage(session, ChatMessage(type = "WAIT", sender = null, content = null))

		val rooms = roomRepository.findBySessionBIsNull()
		if (rooms.isNotEmpty()) {
			roomRepository.save(rooms[0].copy(sessionB = session.id))
		}

		if (rooms.isEmpty()) {
			roomRepository.save(
				Room(
					id = null,
					sessionA = session.id,
					sessionB = null,
				),
			)
		}

		sessions.add(session)

		val room = roomRepository.findBySessionAOrSessionB(session.id, session.id)
		if (room.get().sessionA != null && room.get().sessionB != null) {
			sessions.find { it.id == room.get().sessionB }.let {
				if (it != null) {
					sendMessage(it, ChatMessage(type = "JOIN", content = null, sender = null))
				}
			}
			sessions.find { it.id == room.get().sessionA }.let {
				if (it != null) {
					sendMessage(it, ChatMessage(type = "JOIN", content = null, sender = null))
				}
			}
		}
	}

	fun closedConnection(session: WebSocketSession) {
		sessions.remove(session)

		val roomEntity = roomRepository.findBySessionAOrSessionB(session.id, session.id)

		if (roomEntity.isPresent) {
			roomRepository.delete(roomEntity.get())

			val roomSessions =
				listOf(
					sessions.find { it.id == roomEntity.get().sessionA },
					sessions.find { it.id == roomEntity.get().sessionB },
				)

			roomSessions.map {
				if (it != null) {
					sendMessage(it, ChatMessage(type = "EXIT", content = null, sender = null))
					it.close()
				}
			}
		}
	}
}
