package kr.entree.kotu.ui.data

import javafx.beans.property.*
import kr.entree.kotu.manager.GameManager
import kr.entree.kotu.network.RoomData
import kr.entree.kotu.network.UserData
import tornadofx.asObservable
import tornadofx.getValue
import tornadofx.objectBinding
import tornadofx.setValue

class Room {
    lateinit var manager: GameManager
    val players = SimpleMapProperty(mutableMapOf<String, RoomPlayer>().asObservable())
    val idProperty = SimpleStringProperty()
    var id by idProperty
    val nameProperty = SimpleStringProperty()
    var name by nameProperty
    val typeProperty = SimpleObjectProperty<GameType>()
    var type by typeProperty
    val maxPlayersProperty = SimpleIntegerProperty()
    var maxPlayers by maxPlayersProperty
    val publicProperty = SimpleBooleanProperty()
    var public by publicProperty
    val typeName = typeProperty.objectBinding { it?.gameName }
    val ingameProperty = SimpleBooleanProperty()
    var ingame by ingameProperty

    operator fun plusAssign(user: User) {
        players[user.id] = RoomPlayer(user)
    }

    fun update(room: RoomData) {
        players.clear()
        room.players.forEach {
            this += manager.users[it] ?: return@forEach
        }
        id = room.id
        name = room.title
        type = room.type
        maxPlayers = room.limit
        public = !room.password
        ingame = room.ingame
    }

    fun update(user: UserData) {
        manager.updateUser(user)
        players[user.id]?.update(user.game)
    }

    fun join(id: String) {
        val user = manager.users[id] ?: return
        this += user
    }

    fun join(userData: UserData) {
        val user = manager.users[userData.id] ?: return
        user.update(userData)
        this += user
    }

    fun quit(id: String) = players.remove(id)
}