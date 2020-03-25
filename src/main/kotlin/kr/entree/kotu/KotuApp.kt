@file:Suppress("EXPERIMENTAL_API_USAGE")

package kr.entree.kotu

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.wss
import io.ktor.client.request.get
import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kr.entree.kotu.codec.kkutukorea.decodeKkutuKorea
import kr.entree.kotu.codec.kkutukorea.encodeKkutuKorea
import kr.entree.kotu.lobby.LobbyController
import kr.entree.kotu.lobby.LobbyStyle
import kr.entree.kotu.lobby.LobbyView
import kr.entree.kotu.standard.substringBetween
import tornadofx.App
import tornadofx.UIComponent
import tornadofx.launch

val mainJson = Json(JsonConfiguration.Stable)

suspend inline fun startWebSocket(url: String, queue: Channel<Frame>, crossinline receiver: (Frame) -> Unit = {}) {
    HttpClient {
        install(WebSockets)
    }.wss(url) {
        val input = launch {
            while (isActive) {
                val frame = incoming.receive()
                withContext(Dispatchers.Main) {
                    receiver(frame)
                }
            }
        }.apply { invokeOnCompletion { it?.printStackTrace() } }
        val output = launch {
            while (isActive) {
                val out = queue.receive()
                send(out)
            }
        }.apply { invokeOnCompletion { it?.printStackTrace() } }
        input.join()
        output.join()
    }
}

suspend inline fun startWebSocket(queue: Channel<Frame>, crossinline receiver: (Frame) -> Unit = {}) {
    val html = HttpClient(Apache).get<String>("https://kkutu.co.kr/?server=1#")
    startWebSocket(html.substringBetween("<span id=\"URL\">", "</span>"), queue, receiver)
}

fun main() {
    launch<KotuApp>()
}

class KotuApp : App(LobbyView::class, LobbyStyle::class) {
    val lobbyController: LobbyController by inject()

    override fun onBeforeShow(view: UIComponent) {
        val channel = Channel<Frame>()
        GlobalScope.launch(Dispatchers.IO) {
            startWebSocket(channel) {
                val packet = decodeKkutuKorea(it)
                lobbyController.onPacket(packet)
            }
        }
        lobbyController.packetSender = {
            val packet = encodeKkutuKorea(it)
            GlobalScope.launch(Dispatchers.IO) {
                channel.send(packet)
            }
        }
    }
}