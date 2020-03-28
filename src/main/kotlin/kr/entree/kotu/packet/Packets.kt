package kr.entree.kotu.packet

import io.ktor.http.URLBuilder
import kotlinx.serialization.json.JsonElement
import kr.entree.kotu.ui.data.GameType

/**
 * Created by JunHyung Lim on 2020-03-28
 */
sealed class Packet {
    sealed class In : Packet() {
        class Unknown(val type: String, val element: JsonElement) : Packet()
        class Chat(val sender: String,val  message: String)
        class Disconnect(val id: String)
        class Error(val code: String)
        class PreRoom(val id: Int, val channel: String)
        class User(val id: String, val name: String, val game: Game)
        class Yell(val message: String)

        class Room(
            val id: String,
            val title: String,
            val type: GameType,
            val limit: Int,
            val password: Boolean,
            val ingame: Boolean,
            val players: Collection<String>
        )

        class Welcome(
            val users: Map<String, User> = mutableMapOf(),
            val rooms: Map<String, Room> = mutableMapOf()
        )
    }

    sealed class Out : Packet() {
        class Chat(val message: String)
        class RoomEnter(val id: Int, val channel: String, val password: String = "")
    }
}

fun Packet.In.PreRoom.webSocketUrl(base: URLBuilder) = URLBuilder(base).apply {
    port = 8515 + channel.toInt()
    encodedPath = "$encodedPath&${channel}&${id}"
}