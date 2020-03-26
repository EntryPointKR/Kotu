package kr.entree.kotu.codec.kkutukorea

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readBytes
import io.ktor.http.cio.websocket.readText
import kotlinx.serialization.json.JsonObject
import kr.entree.kotu.codec.getStringWhileZero
import kr.entree.kotu.codec.getUnsignedByte
import kr.entree.kotu.data.GameType
import kr.entree.kotu.data.Room
import kr.entree.kotu.data.User
import kr.entree.kotu.mainJson
import kr.entree.kotu.packet.Empty
import kr.entree.kotu.packet.Unknown
import kr.entree.kotu.packet.input.*
import java.nio.ByteBuffer

fun Frame.Text.decodeJson(): Any {
    val element = mainJson.parseJson(readText()).jsonObject
    return when (val type = element["type"]!!.primitive.content) {
        "welcome" -> Welcome().apply {
            element["users"]?.jsonObject?.forEach { (id, element) ->
                users[id] = decodeUser(element.jsonObject)
            }
            element["rooms"]?.jsonObject?.forEach { (id, element) ->
                rooms[id] = decodeRoom(element.jsonObject)
            }
        }
        "conn" -> decodeUser(element["user"]!!.jsonObject)
        "disconn" -> Disconnect(element["id"]!!.primitive.content)
        "room" -> decodeRoom(element["room"]!!.jsonObject)
        "preRoom" -> PreRoom(element["id"]!!.primitive.int, element["channel"]?.primitive?.content ?: "5")
        else -> Unknown(type, element)
    }
}

fun decodeKkutuKorea(frame: Frame): Any = when (frame) {
    is Frame.Text -> frame.decodeJson()
    is Frame.Binary -> frame.decodeBinaryChat()
    else -> Empty
}

fun decodeUser(json: JsonObject): User {
    val id = json["id"]!!.primitive.content
    val nick = json["profile"]?.jsonObject?.get("nick")?.primitive?.content ?: "알 수 없음"
    return User(id, nick)
}

fun decodeRoom(json: JsonObject): Room {
    val id = json["id"]!!.primitive.content
    val name = json["title"]?.primitive?.content ?: "???"
    val type = runCatching {
        GameType.values()[json["mode"]?.primitive?.int ?: 0 + 1]
    }.getOrElse {
        GameType.UNKNOWN
    }
    val private = json["password"]?.primitive?.boolean ?: false
    return Room(id, name, type, !private)
}

fun Frame.Binary.decodeBinaryChat() = ByteBuffer.wrap(readBytes()).run {
    get() // Discard first byte idk why
    val type = ChatType.fromId(get().toInt())
    val sender = getStringWhileZero()
    var chat = ""
    if (type == ChatType.NOTICE) {
        val code = getUnsignedByte()
    } else {
        chat = getStringWhileZero()
    }
    if (type == ChatType.WHISPER) {
        val to = getStringWhileZero()
    } else if (type == ChatType.DATA) {
        val data = getStringWhileZero()
    }
    Chat(sender, chat)
}