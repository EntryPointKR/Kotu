package kr.entree.kotu.codec.kkutukorea

import io.ktor.http.cio.websocket.Frame
import kr.entree.kotu.codec.Codec

/**
 * Created by JunHyung Lim on 2020-03-27
 */
object KkutuKorea : Codec {
    val ENCODER = KkutuKoreaEncoder()
    val DECODER = KkutuKoreaDecoder()

    override fun encode(packet: Any) = ENCODER.encode(packet)

    override fun decode(frame: Frame) = DECODER.decode(frame)

    object PacketType {
        const val CHAT = 2
    }
}