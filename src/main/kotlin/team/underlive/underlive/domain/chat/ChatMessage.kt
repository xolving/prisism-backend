package team.underlive.underlive.domain.chat

class ChatMessage (
	val messageType: MessageType,
	val senderId: String,
	var message: String
){
	enum class MessageType {
		ENTER, TALK
	}
}