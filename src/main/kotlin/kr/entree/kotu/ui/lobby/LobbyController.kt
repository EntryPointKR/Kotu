package kr.entree.kotu.ui.lobby

import io.ktor.http.URLBuilder
import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.entree.kotu.codec.Codec
import kr.entree.kotu.manager.GameManager
import kr.entree.kotu.packet.Unknown
import kr.entree.kotu.packet.inbound.*
import kr.entree.kotu.packet.outbound.ChatOut
import kr.entree.kotu.packet.outbound.RoomEnter
import kr.entree.kotu.retrieveWebSocketUrl
import kr.entree.kotu.startWebSocket
import kr.entree.kotu.ui.data.GameRoom
import kr.entree.kotu.ui.data.Room
import kr.entree.kotu.ui.data.User
import kr.entree.kotu.ui.room.RoomController
import kr.entree.kotu.ui.room.RoomView
import kr.entree.kotu.ui.wizard.awaitTextInput
import tornadofx.Controller
import tornadofx.singleAssign
import kotlin.collections.set

class LobbyController : Controller() {
    val view by inject<LobbyView>()
    val codec by param<Codec>()
    val gameManager = GameManager()
    val users get() = gameManager.users
    val rooms get() = gameManager.rooms
    var packetSender: (Any) -> Unit by singleAssign()
    var url: URLBuilder by singleAssign()

    fun connect() {
        val channel = Channel<Frame>()
        GlobalScope.launch(Dispatchers.IO) {
            val wsUrl = retrieveWebSocketUrl()
            url = URLBuilder(wsUrl)
            startWebSocket(wsUrl, channel) {
                val packet = codec.decode(it)
                withContext(Dispatchers.Main) {
                    onPacket(packet)
                }
            }
        }
        packetSender = {
            val packet = codec.encode(it)
            GlobalScope.launch(Dispatchers.IO) {
                channel.send(packet)
            }
        }
    }

    fun onPacket(packet: Any) {
        when (packet) {
            is Chat -> gameManager.users[packet.sender]?.apply {
                view.chat("$name: ${packet.message}")
            }
            is Welcome -> gameManager.init(packet)
            is Room -> {
                if (packet.userIds.isEmpty()) {
                    gameManager.rooms.remove(packet.id)
                    return
                }
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
            is Yell -> view.chat("[공지] ${packet.message}")
            else -> System.err.println("Not processed packet: ${packet.javaClass.name} $packet")
        }
    }

    fun chat(message: String) {
        packetSender(ChatOut(message))
    }

    fun join(room: Room) {
        if (room.public) {
            packetSender(RoomEnter(room.id.toInt(), "4"))
            return
        }
        GlobalScope.launch(Dispatchers.JavaFx) {
            val password = awaitTextInput("방 비밀번호")
            packetSender(RoomEnter(room.id.toInt(), "4", password))
        }
    }

    fun join(preRoom: PreRoom) {
        val room = gameManager.rooms[preRoom.id.toString()] ?: return
        setInScope(
            RoomController(
                GameRoom(room, gameManager),
                preRoom.webSocketUrl(url),
                codec
            ), scope
        )
        find<RoomView>().apply {
            title = "${room.id} 번방"
        }.openWindow()
    }
}