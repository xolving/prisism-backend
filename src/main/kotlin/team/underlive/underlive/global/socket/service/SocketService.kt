package team.underlive.underlive.global.socket.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import team.underlive.underlive.domain.room.entity.RoomEntity
import team.underlive.underlive.domain.room.repository.RoomRepository
import team.underlive.underlive.domain.session.entity.SessionEntity
import team.underlive.underlive.domain.session.repository.SessionRepository
import team.underlive.underlive.global.socket.dto.ChatMessage
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class SocketService(
	private val roomRepository: RoomRepository,
	private val sessionRepository: SessionRepository,
	private val objectMapper: ObjectMapper
){
	val sessions = ConcurrentHashMap<UUID, WebSocketSession>()

	fun sendMessage(session: WebSocketSession, message: ChatMessage) {
		session.sendMessage(TextMessage(objectMapper.writeValueAsString(message)))
	}

	@Transactional
	fun handlerActions(session: WebSocketSession, chatMessage: ChatMessage) {
		val sessionEntity = sessionRepository.findBySocket(UUID.fromString(session.id))

		if (sessionEntity.isPresent) {
			val roomEntity = roomRepository.findBySessionsContains(sessionEntity.get())

			roomEntity.get().sessions.map { mapSession ->
				val roomSession = sessions.filter { it.key == mapSession.socket }.toList()[0]
				if(session.id != roomSession.first.toString()){
					sendMessage(roomSession.second, chatMessage)
				}
			}
		}
	}

	@Transactional
	fun establishedConnection(session: WebSocketSession){
		session.sendMessage(TextMessage("{\"status\":\"WAIT\"}"))

		val sessionEntity = SessionEntity(null, UUID.fromString(session.id))
		sessionRepository.save(sessionEntity)

		sessions[UUID.fromString(session.id)] = session

		val rooms = roomRepository.findRoomsWithSingleSession()

		if(rooms.isEmpty()){
			val roomEntity = RoomEntity(null, mutableListOf())
			rooms.add(roomEntity)
		}

		val room = rooms[0]
		room.sessions.add(sessionEntity)
		roomRepository.save(room)

		if(room.sessions.size == 2){
			room.sessions.map { mapSession ->
				sessions.filter { run {
					it.key == mapSession.socket
				}}.map { run {
					it.value.sendMessage(TextMessage("{\"status\":\"JOIN\"}"))
				}}
			}
		}
	}

	@Transactional
	fun closedConnection(session: WebSocketSession){
		sessions.remove(UUID.fromString(session.id))

		val sessionEntity = sessionRepository.findBySocket(UUID.fromString(session.id))
		val roomEntity = roomRepository.findBySessionsContains(sessionEntity.get()).get()

		roomEntity.sessions.map { mapSession ->
			val roomSessions = sessions.filter { it.key == mapSession.socket }
			roomSessions.map {
				it.value.sendMessage(TextMessage("{\"status\":\"EXIT\"}"))
				it.value.close()
			}
		}

		sessionRepository.delete(sessionEntity.get())
		roomRepository.deleteBySessionsContains(sessionEntity.get())
	}
}