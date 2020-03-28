package kr.entree.kotu.ui.data

import javafx.beans.property.*
import kr.entree.kotu.manager.GameManager
import kr.entree.kotu.packet.Packet
import tornadofx.asObservable
import tornadofx.getValue
import tornadofx.objectBinding
import tornadofx.setValue

fun roomOf(packet: Packet.In.Room) =
    Room().apply {
        update(packet)
    }

class Room {
    lateinit var manager: GameManager
    val players = SimpleListProperty(mutableListOf<GamePlayer>().asObservable())
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

    fun update(room: Packet.In.Room) {
        players.setAll(room.players.mapNotNull { manager.users[it]?.toGamePlayer() })
        id = room.id
        name = room.title
        type = room.type
        public = !room.password
        ingame = room.ingame
    }
}