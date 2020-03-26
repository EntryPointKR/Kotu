package kr.entree.kotu.ui.lobby

import io.ktor.http.URLBuilder
import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kr.entree.kotu.packet.Unknown
import kr.entree.kotu.packet.input.Chat
import kr.entree.kotu.packet.input.Disconnect
import kr.entree.kotu.packet.input.PreRoom
import kr.entree.kotu.packet.input.Welcome
import kr.entree.kotu.packet.output.LobbyChat
import kr.entree.kotu.packet.output.RoomEnter
import kr.entree.kotu.startWebSocket
import kr.entree.kotu.ui.data.Room
import kr.entree.kotu.ui.data.User
import kr.entree.kotu.ui.room.RoomView
import tornadofx.Controller
import tornadofx.singleAssign

class LobbyController : Controller() {
    val lobbyView = find<LobbyView>()
    private val gameManager get() = lobbyView.gameManager
    var packetSender: (Any) -> Unit by singleAssign()
    var url: URLBuilder by singleAssign()

    fun onPacket(packet: Any) {
        when (packet) {
            is Chat -> gameManager.users[packet.sender]?.apply {
                lobbyView.chat("$name: ${packet.message}")
            }
            is Welcome -> gameManager.init(packet)
            is Room -> {
                val original = gameManager.rooms[packet.id]
                if (original != null) {
                    original.update(packet)
                } else {
                    gameManager.rooms[packet.id] = packet
                }
            }
            is User -> {
                val original = gameManager.users[packet.id]
                if (original != null) {
                    original.update(packet)
                } else {
                    gameManager.users[packet.id] = packet
                }
            }
            is Disconnect -> gameManager.users.remove(packet.id)
            is Unknown -> System.err.println("Uncaught packet type: ${packet.type} - ${packet.element}")
            is PreRoom -> join(packet)
            else -> System.err.println("Not processed packet: ${packet.javaClass.name} $packet")
        }
    }

    fun chat(message: String) {
        packetSender(LobbyChat(message))
    }

    fun join(room: Room) {
        packetSender(RoomEnter(room.id.toInt(), "4"))
    }

    fun roomUrl(room: PreRoom) = URLBuilder(url).apply {
        port = 8151 + room.channel.toInt()
    }

    fun join(preRoom: PreRoom) {
        val room = gameManager.rooms[preRoom.id.toString()] ?: return
        RoomView(room, gameManager).openWindow()
        val channel = Channel<Frame>()
        GlobalScope.launch(Dispatchers.IO) {
            startWebSocket(URLBuilder(url).buildString(), channel) {

            }
        }
    }
}