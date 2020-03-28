package kr.entree.kotu.packet

/**
 * Created by JunHyung Lim on 2020-03-28
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