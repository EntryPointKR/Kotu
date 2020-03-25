package kr.entree.kotu.manager

import kr.entree.kotu.data.Room
import kr.entree.kotu.data.User
import kr.entree.kotu.packet.input.Welcome

class GameManager {
    val users = mutableMapOf<String, User>()
    val rooms = mutableMapOf<String, Room>()

    fun init(welcome: Welcome) {
        users.clear()
        rooms.clear()
        users += welcome.users
        rooms += welcome.rooms
    }
}