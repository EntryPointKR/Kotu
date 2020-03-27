package kr.entree.kotu.packet.input

import io.ktor.http.URLBuilder

/**
 * Created by JunHyung Lim on 2020-03-26
 */
class PreRoom(val id: Int, val channel: String) {
    fun webSocketUrl(base: URLBuilder) = URLBuilder(base).apply {
        port = 8515 + channel.toInt()
        encodedPath = "$encodedPath&${channel}&${id}"
    }
}