package kr.entree.kotu.lobby

import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.c
import tornadofx.cssclass

class LobbyStyle : Stylesheet() {
    companion object {
        val privateRoom by cssclass()
        val publicRoom by cssclass()
    }

    init {
        privateRoom {
            backgroundColor += Color.LIGHTGRAY
            and(selected) {
                backgroundColor += c("#0096C9", .5)
            }
        }
    }
}