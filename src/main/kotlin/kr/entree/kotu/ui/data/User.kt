package kr.entree.kotu.ui.data

import javafx.beans.property.SimpleStringProperty
import tornadofx.getValue
import tornadofx.setValue

class User(id: String, name: String) {
    val idProperty = SimpleStringProperty(id)
    var id by idProperty
    val nameProperty = SimpleStringProperty(name)
    var name by nameProperty

    companion object {
        val EMPTY = User("-1", "Unknown")
    }

    fun update(user: User) {
        id = user.id
        name = user.name
    }
}