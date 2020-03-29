package kr.entree.kotu.network.codec.kkutukorea

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readBytes
import kotlinx.serialization.json.JsonObject
import kr.entree.kotu.network.UserData
import kr.entree.kotu.network.codec.Codec
import kr.entree.kotu.network.codec.getStringWhileZero
import kr.entree.kotu.network.codec.getUnsignedByte
import kr.entree.kotu.network.packet.ChatType
import kr.entree.kotu.network.packet.Game
import kr.entree.kotu.network.packet.Packet
import java.nio.ByteBuffer

/**
 * Created by JunHyung Lim on 2020-03-27
 */
sealed class KkutuKorea : Codec {
    object Lobby : KkutuKorea() {
        val ENCODER = KkutuKoreaLobbyEncoder()
        val DECODER = KkutuKoreaLobbyDecoder()
        override fun encode(packet: Packet) = ENCODER.encode(packet)
        override fun decode(frame: Frame) = DECODER.decode(frame)
    }

    object Play : KkutuKorea() {
        val ENCODER = KkutuKoreaPlayEncoder()
        val DECODER = KkutuKoreaPlayDecoder()
        override fun encode(packet: Packet) = ENCODER.encode(packet)
        override fun decode(frame: Frame) = DECODER.decode(frame)
    }

    object PacketType {
        const val CHAT = 2
    }

    companion object {
        val ENVIRONMENT = Environment(Lobby, Play)

        fun decodeBinaryChat(binary: Frame.Binary) = ByteBuffer.wrap(binary.readBytes()).run {
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
            Packet.In.Lobby.Chat(sender, chat)
        }

        fun decodeGame(json: JsonObject): Game {
            val ready = json["ready"]?.primitive?.boolean ?: false
            return Game(ready)
        }

        fun decodeUserData(json: JsonObject): UserData {
            val id = json["id"]!!.primitive.content
            val nick = json["profile"]?.jsonObject?.get("nick")?.primitive?.content ?: "알 수 없음"
            val game = json["game"]!!.jsonObject
            return UserData(id, nick, decodeGame(game))
        }
    }
}