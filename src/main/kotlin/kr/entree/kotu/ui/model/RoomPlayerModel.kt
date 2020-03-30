package kr.entree.kotu.ui.model

import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableBooleanValue
import kr.entree.kotu.ui.data.RoomPlayer
import tornadofx.ItemViewModel
import tornadofx.stringBinding

/**
 * Created by JunHyung Lim on 2020-03-30
 */
class RoomPlayerModel(player: RoomPlayer) : ItemViewModel<RoomPlayer>(player) {
    val name: SimpleStringProperty = bind(RoomPlayer::nameProperty)
    val ready: ObservableBooleanValue = bind(RoomPlayer::readyProperty)
    val readyText = ready.stringBinding { if (it == true) "준비" else "대기" }
}