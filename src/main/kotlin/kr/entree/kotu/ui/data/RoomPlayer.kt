package kr.entree.kotu.ui.data

import javafx.beans.property.SimpleBooleanProperty
import kr.entree.kotu.network.packet.Game
import tornadofx.getValue
import tornadofx.setValue
import tornadofx.stringBinding

/**
 * Created by JunHyung Lim on 2020-03-29
 */
data class RoomPlayer(val user: User) {
    val nameProperty get() = user.nameProperty
    val readyProperty = SimpleBooleanProperty()
    var ready by readyProperty

    fun update(game: Game) {
        ready = game.ready
    }

    companion object {
        val EMPTY = RoomPlayer(User.EMPTY).apply {
            ready = true
        }
    }
}