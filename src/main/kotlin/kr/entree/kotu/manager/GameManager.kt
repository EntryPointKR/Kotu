package kr.entree.kotu.manager

import kr.entree.kotu.packet.Packet
import kr.entree.kotu.ui.data.Room
import kr.entree.kotu.ui.data.User
import kr.entree.kotu.ui.data.roomOf
import kr.entree.kotu.ui.data.userOf
import tornadofx.asObservable

class GameManager {
    val users = mutableMapOf<String, User>().asObservable()
    val rooms = mutableMapOf<String, Room>().asObservable()

    operator fun plusAssign(user: User) {
        users[user.id] = user.apply {
            manager = this@GameManager
        }
    }

    operator fun plusAssign(room: Room) {
        rooms[room.id] = room.apply {
            manager = this@GameManager
        }
    }

    fun init(welcomePacket: Packet.In.Welcome) {
        users.clear()
        rooms.clear()
        welcomePacket.users.forEach { (_, user) ->
            this += userOf(user)
        }
        welcomePacket.rooms.forEach { (_, room) ->
            this += roomOf(room)
        }
    }
}