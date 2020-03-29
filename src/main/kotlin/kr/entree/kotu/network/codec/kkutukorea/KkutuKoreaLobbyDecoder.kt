package kr.entree.kotu.network.codec.kkutukorea

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.content
import kr.entree.kotu.network.RoomData
import kr.entree.kotu.network.codec.StandardDecoder
import kr.entree.kotu.network.packet.Packet
import kr.entree.kotu.ui.data.GameType

/**
 * Created by JunHyung Lim on 2020-03-27
 */
class KkutuKoreaLobbyDecoder : StandardDecoder() {
    override fun decodeJson(json: JsonObject): Packet {
        return when (val type = json["type"]!!.content) {
            "welcome" -> {
                val users = json["users"]?.jsonObject?.map { it ->
                    it.key to KkutuKorea.decodeUserData(it.value.jsonObject)
                }?.toMap() ?: emptyMap()
                val rooms = json["rooms"]?.jsonObject?.map {
                    it.key to decodeRoomData(it.value.jsonObject)
                }?.toMap() ?: emptyMap()
                Packet.In.Lobby.Welcome(users, rooms)
            }
            "conn" -> Packet.In.Lobby.Join(KkutuKorea.decodeUserData(json["user"]!!.jsonObject))
            "disconn" -> Packet.In.Lobby.Quit(json["id"]!!.primitive.content)
            "room" -> Packet.In.Lobby.Room(decodeRoomData(json["room"]!!.jsonObject))
            "user" -> Packet.In.Lobby.User(KkutuKorea.decodeUserData(json["user"]!!.jsonObject))
            "preRoom" -> Packet.In.Lobby.PreRoom(
                json["id"]!!.primitive.int,
                json["channel"]?.primitive?.content ?: "5"
            )
            "yell" -> Packet.In.Lobby.Yell(json["value"]?.primitive?.content ?: "null")
            else -> Packet.In.Unknown(type, json)
        }
    }

    fun decodeRoomData(json: JsonObject): RoomData {
        val id = json["id"]!!.primitive.content
        val name = json["title"]?.primitive?.content ?: "???"
        val type = runCatching {
            GameType.values()[json["mode"]?.primitive?.int ?: 0 + 1]
        }.getOrElse {
            GameType.UNKNOWN
        }
        val maxPlayers = json["limit"]?.primitive?.int ?: 1
        val private = json["password"]?.primitive?.boolean ?: false
        val ingame = json["gaming"]?.primitive?.boolean ?: false
        val userIds = json["players"]?.jsonArray?.map {
            if (it is JsonPrimitive) {
                it.primitive.content
            } else "Bot"
        } ?: emptyList()
        val readies = json["readies"]?.jsonObject?.mapNotNull { (id, value) ->
            if ((value as? JsonObject)?.get("r")?.primitive?.boolean == true)
                id
            else
                null
        } ?: emptyList()
        return RoomData(id, name, type, maxPlayers, private, ingame, userIds, readies)
    }
}