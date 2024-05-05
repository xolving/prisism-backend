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

	@Transactional
	fun estabilshedConnection(session: WebSocketSession){
		session.sendMessage(TextMessage("{\"status\":\"WAIT\"}"))

		val sessionEntity = SessionEntity(null, UUID.fromString(session.id))
		sessionRepository.save(sessionEntity)

		sessions[UUID.fromString(session.id)] = session

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
			sessions.map { (key, value) -> run {
				val currentSession = sessionRepository.findBySocket(key)
				if(rooms[0].sessions.isNotEmpty() && rooms[0].sessions.contains(currentSession.get())){
					value.sendMessage(TextMessage("{\"status\":\"JOIN\"}"))
				}
			}}
		}
	}

	@Transactional
	fun closedConnection(session: WebSocketSession){
		sessions.remove(UUID.fromString(session.id))

		val sessionEntity = sessionRepository.findBySocket(UUID.fromString(session.id))

		if(sessionEntity.isPresent){
			val roomEntity = roomRepository.findBySessionsContains(sessionEntity.get())

			sessions.map { (key, value) -> run {
				val currentSession = sessionRepository.findBySocket(key)
				if(roomEntity.get().sessions.contains(currentSession.get())){
					value.sendMessage(TextMessage("{\"status\":\"EXIT\"}"))
					value.close()
					sessionRepository.delete(currentSession.get())
				}
			}}

			roomRepository.deleteBySessionsContains(sessionEntity.get())
		}
	}
}