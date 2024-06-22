package team.underlive.underlive.global.socket.dto

import com.fasterxml.jackson.annotation.ObjectIdGenerators.StringIdGenerator

class ChatMessage (
	val message: String?,
	val ping: String?,
	val sender: String?,
)