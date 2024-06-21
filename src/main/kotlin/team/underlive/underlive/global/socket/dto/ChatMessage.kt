package team.underlive.underlive.global.socket.dto

import com.fasterxml.jackson.annotation.ObjectIdGenerators.StringIdGenerator

class ChatMessage (
	var message: String?,
	var ping: String?
)