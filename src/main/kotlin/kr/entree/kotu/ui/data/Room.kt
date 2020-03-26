package kr.entree.kotu.ui.data

import javafx.beans.property.*
import tornadofx.asObservable
import tornadofx.getValue
import tornadofx.objectBinding
import tornadofx.setValue

class Room(
    id: String,
    name: String,
    type: GameType,
    maxPlayers: Int,
    public: Boolean,
    ingame: Boolean,
    userIds: Collection<String>
) {
    val userIds = SimpleListProperty(ArrayList(userIds).asObservable())
    val idProperty = SimpleStringProperty(id)
    var id by idProperty
    val nameProperty = SimpleStringProperty(name)
    var name by nameProperty
    val typeProperty = SimpleObjectProperty(type)
    var type by typeProperty
    val maxPlayersProperty = SimpleIntegerProperty(maxPlayers)
    var maxPlayers by maxPlayersProperty
    val publicProperty = SimpleBooleanProperty(public)
    var public by publicProperty
    val typeName = typeProperty.objectBinding { it?.gameName }
    val ingameProperty = SimpleBooleanProperty(ingame)
    var ingame by ingameProperty

    fun update(room: Room) {
        userIds.setAll(room.userIds)
        id = room.id
        name = room.name
        type = room.type
        public = room.public
        ingame = room.ingame
    }
}