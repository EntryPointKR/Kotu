package kr.entree.kotu.data

class Room(
    val id: String,
    val name: String,
    val type: GameType,
    var public: Boolean
) {
    val typeName get() = type.gameName
}