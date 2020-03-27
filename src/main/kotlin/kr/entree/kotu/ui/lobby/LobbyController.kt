package kr.entree.kotu.ui.lobby

import io.ktor.http.URLBuilder
import io.ktor.http.cio.websocket.Frame
import javafx.collections.ObservableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.entree.kotu.codec.kkutukorea.decodeKkutuKorea
import kr.entree.kotu.codec.kkutukorea.encodeKkutuKorea
import kr.entree.kotu.manager.GameManager
import kr.entree.kotu.packet.Unknown
import kr.entree.kotu.packet.input.Chat
import kr.entree.kotu.packet.input.Disconnect
import kr.entree.kotu.packet.input.PreRoom
import kr.entree.kotu.packet.input.Welcome
import kr.entree.kotu.packet.output.LobbyChat
import kr.entree.kotu.packet.output.RoomEnter
import kr.entree.kotu.retrieveWebSocketUrl
import kr.entree.kotu.startWebSocket
import kr.entree.kotu.ui.data.Room
import kr.entree.kotu.ui.data.User
import kr.entree.kotu.ui.room.RoomView
import tornadofx.Controller
import tornadofx.bind
import tornadofx.singleAssign

class LobbyController : Controller() {
    val view by inject<LobbyView>()
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
                val packet = decodeKkutuKorea(it)
                withContext(Dispatchers.Main) {
                    onPacket(packet)
                }
            }
        }
        packetSender = {
            val packet = encodeKkutuKorea(it)
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

    fun bindUsers(strings: ObservableList<String>) {
        strings.bind(gameManager.users) { _, user -> user.name }
    }

    fun bindRooms(rooms: ObservableList<Room>) {
        rooms.bind(gameManager.rooms) { _, room -> room }
    }

    fun chat(message: String) {
        packetSender(LobbyChat(message))
    }

    fun join(room: Room) {
        packetSender(RoomEnter(room.id.toInt(), "4"))
    }

    fun join(preRoom: PreRoom) {
        val room = gameManager.rooms[preRoom.id.toString()] ?: return
        val connection = startWebSocket(preRoom.webSocketUrl(url).buildString()) {
            val decode = decodeKkutuKorea(it)
            withContext(Dispatchers.Main) {

            }
        }
        RoomView(room, gameManager, connection).apply {
            openWindow()
        }
    }
}