package kr.entree.kotu.ui.component

import javafx.scene.control.Label
import javafx.scene.paint.Color
import kr.entree.kotu.ui.model.RoomPlayerModel
import tornadofx.*

/**
 * Created by JunHyung Lim on 2020-03-26
 */
class PlayerCard : Fragment() {
    val player: RoomPlayerModel by param()
    var readyLabel by singleAssign<Label>()

    override val root = hbox {
        vbox {
            stackpane {
                imageview("/images/body/def.png")
                imageview("/images/eyes/def.png")
                imageview("/images/mouth/def.png")
            }
            text(player.name)
            readyLabel = label(player.readyText)
            player.ready.onChange {
                readyLabel.textFill = if (it) Color.BLUE else Color.BLACK
            }
        }
    }
}