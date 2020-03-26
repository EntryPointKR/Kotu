package kr.entree.kotu.ui.lobby

import javafx.scene.control.ListView
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.stage.Screen
import kr.entree.kotu.manager.GameManager
import kr.entree.kotu.ui.data.Room
import tornadofx.*

class LobbyView : View() {
    val controller: LobbyController by inject()
    var userView: ListView<String> by singleAssign()
    var roomView: TableView<Room> by singleAssign()
    var chatArea: TextArea by singleAssign()
    var chatField: TextField by singleAssign()
    val gameManager = GameManager()

    override val root = borderpane {
        left = listview<String> {
            items.bind(gameManager.users) { _, user -> user.name }
            userView = this
        }
        center = tableview<Room> {
            column("#", Room::idProperty)
            column("이름", Room::nameProperty)
            column("종류", Room::typeName)
            column("공개", Room::publicProperty).cellFormat {
                tableRow.toggleClass(LobbyStyle.publicRoom, it)
                tableRow.toggleClass(LobbyStyle.privateRoom, !it)
                text = if (it) "공개" else "비공개"
            }
            onDoubleClick {
                val room = selectedItem ?: return@onDoubleClick
                controller.join(room)
            }
            items.bind(gameManager.rooms) { _, room -> room }
            smartResize()
            roomView = this
        }
        bottom = vbox {
            chatArea = textarea {
                isEditable = false
            }
            chatField = textfield {
                setOnKeyPressed {
                    if (text.isNotBlank() && it.code == KeyCode.ENTER) {
                        controller.chat(text)
                        text = ""
                    }
                }
            }
        }
        Screen.getPrimary().bounds.let { rectangle ->
            minWidth = rectangle.width / 2
            minHeight = rectangle.height / 2
            primaryStage.x = minWidth / 2
            primaryStage.y = minHeight / 2
        }
    }

    fun chat(message: String) {
        chatArea.appendText(message)
        chatArea.appendText("\n")
    }
}