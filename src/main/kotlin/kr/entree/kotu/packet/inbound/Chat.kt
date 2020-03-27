package kr.entree.kotu.packet.inbound

class Chat(
    val sender: String,
    val message: String
)

/*
data: 5
default: 1
game: 3
notice: 4
whisper: 2
 */

enum class ChatType(val id: Int) {
    DEFAULT(1),
    WHISPER(2),
    GAME(3),
    NOTICE(4),
    DATA(5);

    companion object {
        fun fromId(id: Int) = values().find { it.id == id }
            ?: throw IllegalArgumentException("Unknown id of chat type: $id")
    }
}