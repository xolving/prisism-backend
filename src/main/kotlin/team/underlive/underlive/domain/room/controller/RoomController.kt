package team.underlive.underlive.domain.room.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import team.underlive.underlive.domain.room.controller.dto.res.PlayerCountResponse
import team.underlive.underlive.domain.room.service.RoomService

@RestController
@RequestMapping("/room")
class RoomController(
	private val roomService: RoomService
) {
	@GetMapping
	fun findAllRoom(): ResponseEntity<Int> {
		val rooms = roomService.findAllRoom()
		return ResponseEntity.ok(rooms)
	}

	@GetMapping("/player")
	fun countAllPlayers(): ResponseEntity<PlayerCountResponse>{
		return ResponseEntity.ok(roomService.countAllPlayers())
	}
}