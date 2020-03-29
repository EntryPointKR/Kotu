package kr.entree.kotu.ui.lobby

import io.ktor.http.URLBuilder
import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.entree.kotu.manager.GameManager
import kr.entree.kotu.network.codec.kkutukorea.Environment
import kr.entree.kotu.network.packet.Packet
import kr.entree.kotu.network.packet.webSocketUrl
import kr.entree.kotu.retrieveWebSocketUrl
import kr.entree.kotu.startWebSocket
import kr.entree.kotu.ui.data.Room
import kr.entree.kotu.ui.room.RoomController
import kr.entree.kotu.ui.room.RoomView
import kr.entree.kotu.ui.wizard.awaitTextInput
import tornadofx.Controller
import tornadofx.singleAssign

class LobbyController : Controller() {
    val view by inject<LobbyView>()
    val environment by param<Environment>()
    val gameManager = GameManager()
    val users get() = gameManager.users
    val rooms get() = gameManager.rooms
    var packetSender: (Packet) -> Unit by singleAssign()
    var url: URLBuilder by singleAssign()

    fun connect() {
        val channel = Channel<Frame>()
        GlobalScope.launch(Dispatchers.IO) {
            val wsUrl = retrieveWebSocketUrl()
            url = URLBuilder(wsUrl)
            startWebSocket(wsUrl, channel) {
                val packet = environment.lobby.decode(it)
                withContext(Dispatchers.Main) {
                    onPacket(packet)
                }
            }
        }
        packetSender = {
            val packet = environment.lobby.encode(it)
            GlobalScope.launch(Dispatchers.IO) {
                channel.send(packet)
            }
        }
    }

    fun onPacket(packet: Packet) {
        when (packet) {
            is Packet.In.Lobby.Chat -> gameManager.users[packet.sender]?.apply {
                view.chat("$name: ${packet.message}")
            }
            is Packet.In.Lobby.Welcome -> gameManager.init(packet)
            is Packet.In.Lobby.Room -> gameManager.updateRoom(packet.data)
            is Packet.In.Lobby.User -> gameManager.updateUser(packet.data)
            is Packet.In.Lobby.Join -> gameManager.updateUser(packet.data)
            is Packet.In.Lobby.Quit -> gameManager.users.remove(packet.id)
            is Packet.In.Unknown -> System.err.println("Uncaught packet type: ${packet.type} - ${packet.element}")
            is Packet.In.Lobby.PreRoom -> join(packet)
            is Packet.In.Lobby.Yell -> view.chat("[공지] ${packet.message}")
            else -> System.err.println("Not processed packet: ${packet.javaClass.name} $packet")
        }
    }

    fun chat(message: String) {
        packetSender(Packet.Out.Chat(message))
    }

    fun join(room: Room) {
        if (room.public) {
            packetSender(Packet.Out.Lobby.RoomEnter(room.id.toInt(), "4"))
            return
        }
        GlobalScope.launch(Dispatchers.JavaFx) {
            val password = awaitTextInput("방 비밀번호")
            packetSender(Packet.Out.Lobby.RoomEnter(room.id.toInt(), "4", password))
        }
    }

    fun join(preRoom: Packet.In.Lobby.PreRoom) {
        val room = gameManager.rooms[preRoom.id.toString()] ?: return
        setInScope(
            RoomController(
                room,
                preRoom.webSocketUrl(url),
                environment
            ), scope
        )
        find<RoomView>().apply {
            title = "[${room.id}] ${room.name}"
        }.openWindow()
    }
}