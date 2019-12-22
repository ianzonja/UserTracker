package cz.mendelu.pef.locationbasedservices

data class ChatMessage(val text: String, val time: String, val senderMail: String, val receiverMail: String, val senderImage: String, val type: String, val viewType: Int) {
}