package kr.entree.kotu.network.codec

import io.ktor.http.cio.websocket.Frame
import kr.entree.kotu.network.packet.Packet

/**
 * Created by JunHyung Lim on 2020-03-27
 */
interface Encoder {
    fun encode(packet: Packet): Frame
}