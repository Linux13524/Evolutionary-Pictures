package de.linus_kloeckner.evolutionary_pictures.images

import de.linus_kloeckner.evolutionary_pictures.utils.distance
import javafx.scene.paint.Color
import kotlin.random.Random

class Pixel(val x: Int, val y: Int, val color: Color) {

    constructor(x: Int, y: Int, r: Double, g: Double, b: Double) : this(x, y, Color(r, g, b, 1.0))

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Pixel || x != other.x || y != other.y || r() != other.r() || g() != other.g() || b() != other.b()) return false
        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + r().hashCode()
        result = 31 * result + g().hashCode()
        result = 31 * result + b().hashCode()
        return result
    }

    fun r() = color.red
    fun g() = color.green
    fun b() = color.blue

    fun match(other: Pixel): Double = color.distance(other.color)

    fun crossover(other: Pixel, splice: Int): Pair<Pixel, Pixel> {
        require(splice in 1..2) { "Bad input value for splice" }
        require(this.x == other.x) { "Pixels position do not match" }
        require(this.y == other.y) { "Pixels position do not match" }

        val newPixel1 = if (splice == 1) Pixel(this.x, this.y, this.r(), other.g(), other.b())
        else Pixel(this.x, this.y, this.r(), this.g(), other.b())

        val newPixel2 = if (splice == 1) Pixel(this.x, this.y, other.r(), this.g(), this.b())
        else Pixel(this.x, this.y, other.r(), other.g(), this.b())

        return Pair(newPixel1, newPixel2)
    }

    companion object {
        fun random(x: Int, y: Int) = Pixel(x, y, Random.nextDouble(), Random.nextDouble(), Random.nextDouble())
    }

}