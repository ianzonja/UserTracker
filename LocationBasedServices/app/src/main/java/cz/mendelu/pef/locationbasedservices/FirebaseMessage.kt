package cz.mendelu.pef.locationbasedservices

data class FirebaseMessage(val text: String, val receiver: String, val sender: String, val timestamp: String) {
}