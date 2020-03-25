package kr.entree.kotu.lobby

import kr.entree.kotu.data.Room
import kr.entree.kotu.data.User
import kr.entree.kotu.packet.Unknown
import kr.entree.kotu.packet.input.Chat
import kr.entree.kotu.packet.input.Disconnect
import kr.entree.kotu.packet.input.Welcome
import kr.entree.kotu.packet.output.LobbyChat
import kr.entree.kotu.packet.output.RoomEnter
import tornadofx.Controller
import tornadofx.singleAssign

class LobbyController : Controller() {
    val lobbyView = find<LobbyView>()
    private val gameManager get() = lobbyView.gameManager
    var packetSender: (Any) -> Unit by singleAssign()

    fun onPacket(packet: Any) {
        when (packet) {
            is Chat -> gameManager.users[packet.sender]?.apply {
                lobbyView.chat("$name: ${packet.message}")
            }
            is Welcome -> {
                gameManager.init(packet)
                lobbyView.update()
            }
            is Room -> gameManager.rooms[packet.id] = packet
            is User -> gameManager.users[packet.id] = packet
            is Disconnect -> gameManager.users.remove(packet.id)
            is Unknown -> System.err.println("Uncaught packet type: ${packet.type} - ${packet.element}")
        }
    }

    fun chat(message: String) {
        packetSender(LobbyChat(message))
    }

    fun join(room: Room) {
        packetSender(RoomEnter(room.id.toInt(), "4"))
    }
}