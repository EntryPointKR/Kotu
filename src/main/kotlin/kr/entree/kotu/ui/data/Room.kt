package kr.entree.kotu.ui.data

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.getValue
import tornadofx.objectBinding
import tornadofx.setValue

class Room(
    id: String,
    name: String,
    type: GameType,
    public: Boolean,
    ingame: Boolean
) {
    val idProperty = SimpleStringProperty(id)
    var id by idProperty
    val nameProperty = SimpleStringProperty(name)
    var name by nameProperty
    val typeProperty = SimpleObjectProperty(type)
    var type by typeProperty
    val publicProperty = SimpleBooleanProperty(public)
    var public by publicProperty
    val typeName = typeProperty.objectBinding { it?.gameName }
    val ingameProperty = SimpleBooleanProperty(ingame)
    var ingame by ingameProperty
}