package kr.entree.kotu.manager

import kr.entree.kotu.network.RoomData
import kr.entree.kotu.network.UserData
import kr.entree.kotu.network.packet.Packet
import kr.entree.kotu.ui.data.Room
import kr.entree.kotu.ui.data.User
import tornadofx.asObservable

class GameManager {
    val users = mutableMapOf<String, User>().asObservable()
    val rooms = mutableMapOf<String, Room>().asObservable()

    fun addUser(user: UserData) = User().apply {
        manager = this@GameManager
        update(user)
        users[user.id] = this
    }

    fun addRoom(room: RoomData) = Room().apply {
        manager = this@GameManager
        update(room)
        rooms[id] = this
    }

    fun updateUser(userData: UserData) =
        users.getOrPut(userData.id) {
            addUser(userData)
        }.apply { update(userData) }

    fun updateRoom(roomData: RoomData) {
        if (roomData.players.isEmpty()) {
            rooms.remove(roomData.id)
            return
        }
        val room = rooms.getOrPut(roomData.id) { addRoom(roomData) }
        roomData.readies.forEach {
            room.players[it]?.ready = true
        }
    }

    fun init(welcomePacket: Packet.In.Lobby.Welcome) {
        users.clear()
        rooms.clear()
        welcomePacket.users.forEach { (_, user) ->
            addUser(user)
        }
        welcomePacket.rooms.forEach { (_, room) ->
            addRoom(room)
        }
    }
}