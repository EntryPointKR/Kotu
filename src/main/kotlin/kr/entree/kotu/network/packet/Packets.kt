package kr.entree.kotu.network.packet

import io.ktor.http.URLBuilder
import kotlinx.serialization.json.JsonElement
import kr.entree.kotu.network.RoomData
import kr.entree.kotu.network.UserData

/**
 * Created by JunHyung Lim on 2020-03-28
 */

sealed class Packet {
    sealed class In : Packet() {
        object Close : In()
        object Ping : In()
        object Pong : In()
        class Chat(val sender: String, val message: String) : In()
        class Unknown(val type: String, val element: JsonElement) : In()

        sealed class Lobby : In() {
            class Chat(val sender: String, val message: String) : Lobby()
            class Join(val data: UserData) : Lobby()
            class Quit(val id: String) : Lobby()
            class PreRoom(val id: Int, val channel: String) : Lobby()
            class User(val data: UserData) : Lobby()
            class Yell(val message: String) : Lobby()

            class Room(
                val data: RoomData
            ) : Lobby()

            class Welcome(
                val users: Map<String, UserData> = mutableMapOf(),
                val rooms: Map<String, RoomData> = mutableMapOf()
            ) : Lobby()
        }

        sealed class Play : In() {
            class Error(val code: String) : Play()
            class Join(val user: UserData) : Play()
            class Quit(val id: String) : Play()
            class User(val user: UserData) : Play()
        }
    }

    sealed class Out : Packet() {
        class Chat(val message: String) : Out()

        sealed class Lobby : Out() {
            class RoomEnter(val id: Int, val channel: String, val password: String = "") : Lobby()
        }

        sealed class Play : Out() {

        }
    }
}

fun Packet.In.Lobby.PreRoom.webSocketUrl(base: URLBuilder) = URLBuilder(base).apply {
    port = 8515 + channel.toInt()
    encodedPath = "$encodedPath&${channel}&${id}"
}