package team.underlive.underlive.schedule

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import team.underlive.underlive.global.socket.dto.ChatMessage
import team.underlive.underlive.global.socket.handler.SocketService

@Service
class SocketPingService(
	private val socketService: SocketService,
) {
	@Scheduled(fixedDelay = 5000)
	fun sendPing() {
		socketService.sessions.map {
			socketService.sendMessage(
				it,
				ChatMessage(
					content = null,
					sender = null,
					type = "PING",
				),
			)
		}
	}
}
