package kr.entree.kotu.codec.kkutukorea

import io.ktor.http.cio.websocket.Frame
import kotlinx.serialization.json.json
import kr.entree.kotu.codec.Encoder
import kr.entree.kotu.packet.outbound.ChatOut
import kr.entree.kotu.packet.outbound.RoomEnter
import java.nio.ByteBuffer

/**
 * Created by JunHyung Lim on 2020-03-27
 */
class KkutuKoreaEncoder : Encoder {
    override fun encode(packet: Any): Frame = when (packet) {
        is ChatOut -> {
            val buffer = ByteBuffer.allocate(packet.message.length * 4 + 16)
            buffer.put(KkutuKorea.PacketType.CHAT.toByte())
            buffer.put(1) // type
            buffer.put(0) // zero for marking end
            buffer.put(Charsets.UTF_8.encode(packet.message))
            buffer.put(0) // zero
            Frame.Binary(true, ByteBuffer.wrap(buffer.array(), 0, buffer.position()))
        }
        is RoomEnter -> {
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