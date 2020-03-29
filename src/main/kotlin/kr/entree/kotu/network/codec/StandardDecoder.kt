package kr.entree.kotu.network.codec

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import kotlinx.serialization.json.JsonObject
import kr.entree.kotu.KotuApp
import kr.entree.kotu.network.codec.kkutukorea.KkutuKorea
import kr.entree.kotu.network.packet.Packet

/**
 * Created by JunHyung Lim on 2020-03-29
 */
abstract class StandardDecoder : Decoder {
    override fun decode(frame: Frame): Packet {
        return when (frame) {
            is Frame.Text -> decodeJson(KotuApp.JSON.parseJson(frame.readText()).jsonObject)
            is Frame.Binary -> decodeBinary(frame)
            is Frame.Ping -> Packet.In.Ping
            is Frame.Pong -> Packet.In.Pong
            is Frame.Close -> Packet.In.Close
        }
    }

    abstract fun decodeJson(json: JsonObject): Packet

    fun decodeBinary(binary: Frame.Binary): Packet {
        return KkutuKorea.decodeBinaryChat(binary)
    }
}