package kr.entree.kotu.ui.component

import kr.entree.kotu.ui.data.GamePlayer
import tornadofx.*

/**
 * Created by JunHyung Lim on 2020-03-26
 */
class PlayerCard(val player: GamePlayer) : Fragment() {
    override val root = hbox {
        vbox {
            stackpane {
                imageview("/images/body/def.png")
                imageview("/images/eyes/def.png")
                imageview("/images/mouth/def.png")
            }
            text(player.user.name)
            label(player.readyText)
        }
    }
}