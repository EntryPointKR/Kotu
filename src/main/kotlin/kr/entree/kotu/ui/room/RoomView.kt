package kr.entree.kotu.ui.room

import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.layout.FlowPane
import tornadofx.*

/**
 * Created by JunHyung Lim on 2020-03-26
 */
class RoomView : View() {
    val controller by inject<RoomController>()
    var chatArea by singleAssign<TextArea>()
    var chatField by singleAssign<TextField>()
    var playerPane by singleAssign<FlowPane>()

    override val root = vbox {
        playerPane = flowpane()
        vbox {
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
    }

    fun chat(message: String) {
        chatArea.appendText(message)
        chatArea.appendText("\n")
    }

    override fun onBeforeShow() {
        controller.bindPlayers(playerPane.children)
        controller.start()
    }

    override fun onUndock() {
        controller.shutdown()
    }
}