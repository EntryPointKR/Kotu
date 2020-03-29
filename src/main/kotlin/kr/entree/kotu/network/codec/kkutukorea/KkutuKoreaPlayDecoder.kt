package kr.entree.kotu.network.codec.kkutukorea

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.content
import kr.entree.kotu.network.codec.StandardDecoder
import kr.entree.kotu.network.packet.Packet

/**
 * Created by JunHyung Lim on 2020-03-29
 */
class KkutuKoreaPlayDecoder : StandardDecoder() {
    override fun decodeJson(json: JsonObject): Packet {
        return when (val type = json["type"]!!.content) {
            "error" -> Packet.In.Play.Error(json["code"]?.primitive?.content ?: "null")
            "connRoom" -> Packet.In.Play.Join(KkutuKorea.decodeUserData(json["user"]!!.jsonObject))
            "disconnRoom" -> Packet.In.Play.Quit(json["id"]!!.content)
            "user" -> Packet.In.Play.User(KkutuKorea.decodeUserData(json))
            else -> Packet.In.Unknown(type, json)
        }
    }
}