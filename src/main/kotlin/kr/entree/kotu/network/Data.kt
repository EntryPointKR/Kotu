package kr.entree.kotu.network

import kr.entree.kotu.network.packet.Game
import kr.entree.kotu.ui.data.GameType

/**
 * Created by JunHyung Lim on 2020-03-29
 */
class UserData(val id: String, val name: String, val game: Game)

class RoomData(
    val id: String,
    val title: String,
    val type: GameType,
    val limit: Int,
    val password: Boolean,
    val ingame: Boolean,
    val players: Collection<String>,
    val readies: Collection<String>
)