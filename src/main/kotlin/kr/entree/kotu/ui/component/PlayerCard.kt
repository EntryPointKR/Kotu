package kr.entree.kotu.ui.component

import javafx.scene.control.Label
import javafx.scene.paint.Color
import kr.entree.kotu.ui.data.RoomPlayer
import kr.entree.kotu.ui.data.User
import tornadofx.*

/**
 * Created by JunHyung Lim on 2020-03-26
 */
class PlayerCard(val player: RoomPlayer) : Fragment() {
    var readyLabel by singleAssign<Label>()

    override val root = hbox {
        vbox {
            stackpane {
                imageview("/images/body/def.png")
                imageview("/images/eyes/def.png")
                imageview("/images/mouth/def.png")
            }
            text(player.user.name)
            readyLabel = label(player.readyText)
            player.readyProperty.onChange {
                readyLabel.textFill = if (it) Color.BLUE else Color.BLACK
            }
        }
    }
}