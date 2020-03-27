package kr.entree.kotu.ui.room

import io.ktor.http.cio.websocket.Frame
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import kr.entree.kotu.manager.GameManager
import kr.entree.kotu.network.Connection
import kr.entree.kotu.ui.component.UserCard
import kr.entree.kotu.ui.data.Room
import kr.entree.kotu.ui.data.User
import tornadofx.*

/**
 * Created by JunHyung Lim on 2020-03-26
 */
class RoomView(
    val room: Room,
    val gameManager: GameManager,
    connection: Connection<Frame>
) : View("${room.id} 번방") {
    val controller: RoomController by inject(scope, "connection" to connection)
    var chatArea: TextArea by singleAssign()
    var chatField: TextField by singleAssign()

    override val root = vbox {
        flowpane {
            bindComponents(room.userIds) {
                UserCard(gameManager.users[it] ?: User.EMPTY)
            }
        }
        vbox {
            chatArea = textarea {
                isEditable = false
            }
            chatField = textfield {
                setOnKeyPressed {
                    if (text.isNotBlank() && it.code == KeyCode.ENTER) {
                        TODO()
                    }
                }
            }
        }
    }

    override fun onUndock() {
        controller.shutdown()
    }
}