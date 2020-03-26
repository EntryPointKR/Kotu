package kr.entree.kotu.manager

import kr.entree.kotu.packet.input.Welcome
import kr.entree.kotu.ui.data.Room
import kr.entree.kotu.ui.data.User
import tornadofx.asObservable

class GameManager {
    val users = mutableMapOf<String, User>().asObservable()
    val rooms = mutableMapOf<String, Room>().asObservable()

    fun init(welcome: Welcome) {
        users.clear()
        rooms.clear()
        users += welcome.users
        rooms += welcome.rooms
    }
}