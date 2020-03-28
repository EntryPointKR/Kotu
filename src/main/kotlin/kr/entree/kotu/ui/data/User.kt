package kr.entree.kotu.ui.data

import javafx.beans.property.SimpleStringProperty
import kr.entree.kotu.manager.GameManager
import kr.entree.kotu.packet.Packet
import tornadofx.getValue
import tornadofx.setValue

fun userOf(user: Packet.In.User) =
    User().apply { update(user) }

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

    fun update(user: Packet.In.User) {
        id = user.id
        name = user.name
    }
}