package kr.entree.kotu.codec.kkutukorea

import io.ktor.http.cio.websocket.Frame
import kotlinx.serialization.json.json
import kr.entree.kotu.packet.output.LobbyChat
import kr.entree.kotu.packet.output.RoomEnter
import java.nio.ByteBuffer

/**
 * Created by JunHyung Lim on 2020-03-24
 */
fun encodeKkutuKorea(packet: Any): Frame =
    when (packet) {
        is LobbyChat -> {
            val buffer = ByteBuffer.allocate(packet.message.length + 16)
            buffer.put(KkutuKoreaPacketType.CHAT.toByte())
            buffer.put(1) // type
            buffer.put(0) // zero for marking end
            buffer.put(Charsets.UTF_8.encode(packet.message))
            buffer.put(0) // zero
            Frame.Binary(true, ByteBuffer.wrap(buffer.array(), 0, buffer.position()))
        }
        is RoomEnter -> {
            Frame.Text(json {
                "type" to "preRoom"
                "id" to packet.id
                "channel" to packet.channel
            }.toString())
        }
        else -> throw NotImplementedError(packet.javaClass.simpleName)
    }