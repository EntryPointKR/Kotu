package kr.entree.kotu.ui.data

import javafx.beans.property.SimpleStringProperty
import kr.entree.kotu.manager.GameManager
import kr.entree.kotu.network.UserData
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

    fun update(data: UserData): User = apply {
        id = data.id
        name = data.name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (idProperty != other.idProperty) return false

        return true
    }

    override fun hashCode(): Int {
        return idProperty.hashCode()
    }
}