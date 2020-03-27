package kr.entree.kotu.packet.inbound

import kr.entree.kotu.ui.data.Room
import kr.entree.kotu.ui.data.User

class Welcome {
    val users = mutableMapOf<String, User>()
    val rooms = mutableMapOf<String, Room>()
}