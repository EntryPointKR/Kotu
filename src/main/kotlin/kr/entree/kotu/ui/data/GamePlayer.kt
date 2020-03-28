package kr.entree.kotu.ui.data

import javafx.beans.property.SimpleBooleanProperty
import tornadofx.getValue
import tornadofx.setValue
import tornadofx.stringBinding

/**
 * Created by JunHyung Lim on 2020-03-28
 */
fun User.toGamePlayer() = GamePlayer(id, this)

class GamePlayer(
    id: String,
    val user: User
) {
    val idProperty get() = user.idProperty
    var id by idProperty
    val readyProperty = SimpleBooleanProperty(false)
    var ready by readyProperty
    val readyText = readyProperty.stringBinding { if (it == true) "준비" else "대기" }

    companion object {
        val EMPTY = GamePlayer("-1", User.EMPTY).apply {
            ready = true
        }
    }
}