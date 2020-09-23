package de.linus_kloeckner.evolutionary_pictures.utils

import javafx.scene.paint.Color
import kotlin.math.pow
import kotlin.math.sqrt

fun Double.percentage() = this / 100.0

private val MAX_POSSIBLE_DISTANCE = sqrt(3.0)
fun Color.distance(other: Color): Double {
    val euclideanDistance = sqrt((this.red - other.red).pow(2.0) +
            (this.green - other.green).pow(2.0) +
            (this.blue - other.blue).pow(2.0))

    return 1 - euclideanDistance / MAX_POSSIBLE_DISTANCE
}

fun <A, B> Pair<A, A>.map(transform: (A) -> B) = Pair(transform(this.first), transform(this.second))
fun <T> Pair<T, T>.forEach(action: (T) -> Unit) {
    action(this.first)
    action(this.second)
}