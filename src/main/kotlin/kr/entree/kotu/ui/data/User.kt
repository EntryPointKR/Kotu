package kr.entree.kotu.ui.data

import javafx.beans.property.SimpleStringProperty
import kr.entree.kotu.manager.GameManager
import kr.entree.kotu.network.UserData
import kr.entree.kotu.network.packet.Game
import tornadofx.getValue
import tornadofx.setValue

class User {
    lateinit var manager: GameManager
    val idProperty = SimpleStringProperty()
    var id by idProperty
    val nameProperty = SimpleStringProperty()
    var name by nameProperty

    companion object {
        val EMPTY = User().apply {
            id = "-1"
            name = "Unknown"
        }
    }

    fun update(data: UserData) {
        id = data.id
        name = data.name
    }
}