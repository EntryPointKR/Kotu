package kr.entree.kotu.network.codec.kkutukorea

import io.ktor.http.cio.websocket.Frame
import kotlinx.serialization.json.json
import kr.entree.kotu.network.codec.Encoder
import kr.entree.kotu.network.packet.Packet
import java.nio.ByteBuffer

/**
 * Created by JunHyung Lim on 2020-03-27
 */
class KkutuKoreaLobbyEncoder : Encoder {
    override fun encode(packet: Packet): Frame = when (packet) {
        is Packet.Out.Chat -> {
            val buffer = ByteBuffer.allocate(packet.message.length * 4 + 16)
            buffer.put(KkutuKorea.PacketType.CHAT.toByte())
            buffer.put(1) // type
            buffer.put(0) // zero for marking end
            buffer.put(Charsets.UTF_8.encode(packet.message))
            buffer.put(0) // zero
            Frame.Binary(true, ByteBuffer.wrap(buffer.array(), 0, buffer.position()))
        }
        is Packet.Out.Lobby.RoomEnter -> {
            Frame.Text(json {
                "type" to "enter"
                "id" to packet.id
                if (packet.password.isNotBlank()) {
                    "password" to packet.password
                }
            }.toString())
        }
        else -> throw NotImplementedError(packet.javaClass.simpleName)
    }
}