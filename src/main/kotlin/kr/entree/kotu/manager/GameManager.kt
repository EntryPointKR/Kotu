package kr.entree.kotu.manager

import javafx.beans.property.SimpleMapProperty
import kr.entree.kotu.network.RoomData
import kr.entree.kotu.network.UserData
import kr.entree.kotu.network.packet.Packet
import kr.entree.kotu.ui.data.Room
import kr.entree.kotu.ui.data.User
import tornadofx.asObservable

class GameManager {
    val users = SimpleMapProperty(mutableMapOf<String, User>().asObservable())
    val rooms = SimpleMapProperty(mutableMapOf<String, Room>().asObservable())

    fun createUser() = User().apply {
        manager = this@GameManager
    }

    fun createRoom() = Room().apply {
        manager = this@GameManager
    }

    fun updateUser(userData: UserData) {
        users.getOrPut(userData.id) { createUser() }.update(userData)
    }

    fun updateRoom(roomData: RoomData) {
        if (roomData.players.isEmpty()) {
            rooms.remove(roomData.id)
            return
        }
        val room = rooms.getOrPut(roomData.id) { createRoom() }.update(roomData)
        roomData.readies.forEach {
            room.players[it]?.ready = true
        }
    }

    fun init(welcomePacket: Packet.In.Lobby.Welcome) {
        users.clear()
        rooms.clear()
        welcomePacket.users.forEach { (_, user) ->
            users[user.id] = createUser().update(user)
        }
        welcomePacket.rooms.forEach { (_, room) ->
            rooms[room.id] = createRoom().update(room)
        }
    }
}