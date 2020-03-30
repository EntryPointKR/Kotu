package kr.entree.kotu.ui.room

import io.ktor.http.URLBuilder
import io.ktor.http.cio.websocket.Frame
import javafx.scene.layout.FlowPane
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.entree.kotu.network.Connection
import kr.entree.kotu.network.codec.kkutukorea.Environment
import kr.entree.kotu.network.packet.Packet
import kr.entree.kotu.startWebSocket
import kr.entree.kotu.ui.component.PlayerCard
import kr.entree.kotu.ui.data.Room
import kr.entree.kotu.ui.data.RoomPlayer
import kr.entree.kotu.ui.data.User
import kr.entree.kotu.ui.model.RoomPlayerModel
import tornadofx.*
import kotlin.text.clear

/**
 * Created by JunHyung Lim on 2020-03-27
 */
class RoomController(
    val room: Room,
    val socketUrl: URLBuilder,
    val environment: Environment
) : Controller() {
    val view by inject<RoomView>()
    var connection: Connection<Frame>? = null

    fun bindPlayers(playerPane: FlowPane) {
        playerPane.bindChildren(room.players) { _, player ->
            find<PlayerCard>(PlayerCard::player to RoomPlayerModel(player ?: RoomPlayer.EMPTY)).root
        }
    }

    fun onPacket(packet: Packet) {
        when (packet) {
            is Packet.In.Chat -> {
                val user = room.manager.users[packet.sender] ?: User.EMPTY
                view.chat("${user.name}: ${packet.message}")
            }
            is Packet.In.Play.Error -> {
                error("에러", "에러코드: ${packet.code}", owner = view.currentWindow)
                view.close()
            }
            is Packet.In.Play.Join -> room.join(packet.user)
            is Packet.In.Play.User -> room.update(packet.user)
            is Packet.In.Play.Quit -> room.quit(packet.id)
            is Packet.In.Unknown -> System.err.println("Unknown packet: ${packet.type} ${packet.element}")
            else -> System.err.println("Uncaught packet: ${packet.javaClass.name}")
        }
    }

    fun chat(message: String) {
        sendPacket(Packet.Out.Chat(message))
    }

    fun sendPacket(packet: Packet) {
        GlobalScope.launch(Dispatchers.IO) {
            val encoded = environment.play.encode(packet)
            connection?.send(encoded)
        }
    }

    fun start() {
        shutdown()
        connection = startWebSocket(socketUrl.buildString()) {
            val decoded = environment.play.decode(it)
            withContext(Dispatchers.Main) {
                onPacket(decoded)
            }
        }
    }

    fun shutdown() {
        connection?.cancel()
    }
}