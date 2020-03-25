@file:Suppress("EXPERIMENTAL_API_USAGE")

package kr.entree.kotu.codec

import java.nio.ByteBuffer

fun ByteBuffer.getUnsignedByte() = get().toUByte()

fun ByteBuffer.getStringWhileZero(): String {
    val offset = position()
    var end = offset
    while (true) {
        val byte = getUnsignedByte().toInt()
        if (byte > 0) {
            end = position()
        } else break
    }
    return Charsets.UTF_8.decode(ByteBuffer.wrap(array().sliceArray(offset until end))).toString()
}