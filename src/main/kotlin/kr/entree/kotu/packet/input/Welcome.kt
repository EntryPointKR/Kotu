package kr.entree.kotu.packet.input

import kr.entree.kotu.ui.data.Room
import kr.entree.kotu.ui.data.User

class Welcome {
    val users = mutableMapOf<String, User>()
    val rooms = mutableMapOf<String, Room>()
}