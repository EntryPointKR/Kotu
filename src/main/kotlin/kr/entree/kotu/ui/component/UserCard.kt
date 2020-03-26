package kr.entree.kotu.ui.component

import kr.entree.kotu.ui.data.User
import tornadofx.*

/**
 * Created by JunHyung Lim on 2020-03-26
 */
class UserCard(val user: User) : Fragment() {
    override val root = hbox {
        vbox {
            stackpane {
                imageview("/images/body/def.png")
                imageview("/images/eyes/def.png")
                imageview("/images/mouth/def.png")
            }
            text(user.nameProperty)
        }
    }
}