package kr.entree.kotu.lobby

import javafx.scene.control.ListView
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.stage.Screen
import kr.entree.kotu.data.Room
import kr.entree.kotu.manager.GameManager
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
            userView = this
        }
        center = tableview<Room> {
            readonlyColumn("#", Room::id)
            readonlyColumn("이름", Room::name)
            readonlyColumn("종류", Room::typeName)
            readonlyColumn("공개", Room::public).cellFormat {
                tableRow.toggleClass(LobbyStyle.publicRoom, it)
                tableRow.toggleClass(LobbyStyle.privateRoom, !it)
                text = if (it) "공개" else "비공개"
            }
            onDoubleClick {
                val room = selectedItem ?: return@onDoubleClick
                controller.join(room)
            }
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

    fun update() {
        userView.items.clear()
        userView.items.addAll(gameManager.users.map { it.value.name })
        roomView.items.clear()
        roomView.items.addAll(gameManager.rooms.values)
    }
}