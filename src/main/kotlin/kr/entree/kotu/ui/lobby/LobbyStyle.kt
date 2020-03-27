package kr.entree.kotu.ui.lobby

import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.c
import tornadofx.cssclass

class LobbyStyle : Stylesheet() {
    companion object {
        val privateRoom by cssclass()
        val publicRoom by cssclass()
        val ingameRoom by cssclass()
    }

    init {
        privateRoom {
            backgroundColor += Color.WHITE.darker()
            and(selected) {
                backgroundColor += c("#0096C9", .5)
            }
        }
        ingameRoom {
            backgroundColor += Color.GRAY
            and(selected) {
                backgroundColor += c("#0096C9", .5)
            }
        }
    }
}