package studio.astroturf.quizzi.domain.model.websocket

sealed class ClientMessage {
    data class CreateRoom(
        val roomName: String,
        val categoryId: Int,
        val gameType: String,
    ) : ClientMessage()

    data class JoinRoom(
        val roomId: String,
    ) : ClientMessage()

    data class RejoinRoom(
        val roomId: String,
    ) : ClientMessage()

    data object PlayerReady : ClientMessage()

    data class PlayerAnswer(
        val answer: Int,
    ) : ClientMessage()

    data class PlayerReconnected(
        val playerId: String,
    ) : ClientMessage()
}
