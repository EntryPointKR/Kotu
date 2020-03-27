package kr.entree.kotu.ui.room

import io.ktor.http.cio.websocket.Frame
import kr.entree.kotu.network.Connection
import tornadofx.Controller

/**
 * Created by JunHyung Lim on 2020-03-27
 */
class RoomController : Controller() {
    val connection: Connection<Frame> by param()

    fun shutdown() = connection.cancel()
}