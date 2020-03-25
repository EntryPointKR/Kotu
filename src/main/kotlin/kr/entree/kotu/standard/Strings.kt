package kr.entree.kotu.standard

/**
 * Created by JunHyung Lim on 2020-03-25
 */
fun String.substringBetween(after: String, before: String) = substringAfter(after).substringBefore(before)