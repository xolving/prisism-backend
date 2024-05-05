package team.underlive.underlive.global.socket.handler

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import team.underlive.underlive.global.socket.dto.ChatMessage
import team.underlive.underlive.global.socket.service.SocketService

@Component
class WebSocketHandler(
	private val objectMapper: ObjectMapper,
	private val socketService: SocketService,
) : TextWebSocketHandler() {
	override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
		val chatMessage: ChatMessage = objectMapper.readValue(message.payload, ChatMessage::class.java)
		socketService.handlerActions(session, chatMessage)
	}

	override fun afterConnectionEstablished(session: WebSocketSession) {
		socketService.establishedConnection(session)
		super.afterConnectionEstablished(session)
	}

	override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
		socketService.closedConnection(session)
		super.afterConnectionClosed(session, status)
	}
}