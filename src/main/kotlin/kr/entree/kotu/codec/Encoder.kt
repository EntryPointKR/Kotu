package kr.entree.kotu.codec

import io.ktor.http.cio.websocket.Frame

/**
 * Created by JunHyung Lim on 2020-03-27
 */
interface Encoder {
    fun encode(packet: Any): Frame
}