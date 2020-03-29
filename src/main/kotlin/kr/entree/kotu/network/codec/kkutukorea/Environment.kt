package kr.entree.kotu.network.codec.kkutukorea

import kr.entree.kotu.network.codec.Codec

/**
 * Created by JunHyung Lim on 2020-03-29
 */
class Environment(
    val lobby: Codec,
    val play: Codec
)