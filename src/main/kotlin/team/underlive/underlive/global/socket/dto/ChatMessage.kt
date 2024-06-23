package team.underlive.underlive.global.socket.dto

import com.fasterxml.jackson.annotation.ObjectIdGenerators.StringIdGenerator

class ChatMessage (
	val content: String?,
	val type: String,
	val sender: String?,
)