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
import kr.entree.kotu.network.Connection
import kr.entree.kotu.standard.substringBetween
import kr.entree.kotu.ui.lobby.LobbyStyle
import kr.entree.kotu.ui.lobby.LobbyView
import tornadofx.App
import tornadofx.launch

suspend inline fun startWebSocket(url: String, queue: Channel<Frame>, crossinline receiver: suspend (Frame) -> Unit) {
    HttpClient {
        install(WebSockets)
    }.wss(url) {
        joinAll(launch {
            while (isActive) {
                val out = queue.receive()
                send(out)
            }
        }.apply {
            invokeOnCompletion { it?.printStackTrace() }
        },
            launch {
                while (isActive) {
                    val frame = incoming.receive()
                    withContext(Dispatchers.Main) {
                        receiver(frame)
                    }
                }
            }.apply { invokeOnCompletion { it?.printStackTrace() } })
    }
}

suspend inline fun retrieveWebSocketUrl() =
    HttpClient(Apache).get<String>("https://kkutu.co.kr/?server=1#").substringBetween(
        "<span id=\"URL\">", "</span>"
    )

inline fun startWebSocket(
    url: String,
    crossinline receiver: suspend CoroutineScope.(Frame) -> Unit = {}
): Connection<Frame> {
    val channel = Channel<Frame>()
    val job = GlobalScope.launch(Dispatchers.IO) {
        startWebSocket(url, channel) {
            receiver(it)
        }
    }
    return Connection(job, channel)
}

fun main() {
    launch<KotuApp>()
}

class KotuApp : App(LobbyView::class, LobbyStyle::class) {
    companion object {
        val JSON = Json(JsonConfiguration.Stable)
    }
}