package de.linus_kloeckner.evolutionary_pictures.images

import javafx.scene.image.PixelReader
import javafx.scene.paint.Color
import kotlin.random.Random

class EvolutionaryPicture : Picture {

    var colorPalette: List<Color>? = null

    constructor(size: Size) : super(size)
    constructor(width: Int, height: Int) : super(width, height)
    constructor(pixelReader: PixelReader, width: Int, height: Int) : super(pixelReader, width, height)

    override fun copy() = EvolutionaryPicture(pixelReader, getIntWidth(), getIntHeight()).also { it.colorPalette = colorPalette }

    fun init() {
        fullIteration { x, y ->
            val newPixel = colorPalette?.random()?.let { Pixel(x, y, it) } ?: Pixel.random(x, y)
            this.setPixel(newPixel)
        }
    }

    fun crossover(other: EvolutionaryPicture): Pair<EvolutionaryPicture, EvolutionaryPicture> {
        val randomSplice = Random.nextInt(1, 3)

        val childPicture1 = EvolutionaryPicture(getSize())
        val childPicture2 = EvolutionaryPicture(getSize())

        fullIteration { x, y ->
            val (newPixel1, newPixel2) = this.getPixel(x, y).crossover(other.getPixel(x, y), randomSplice)
            childPicture1.setPixel(newPixel1)
            childPicture2.setPixel(newPixel2)
        }

        return Pair(childPicture1, childPicture2)
    }

    fun mutate(mutationRate: Double) {
        fullIteration { x, y ->
            if (Random.nextDouble() < mutationRate) {
                val newPixel = colorPalette?.random()?.let { Pixel(x, y, it) } ?: Pixel.random(x, y)
                this.setPixel(newPixel)
            }
        }
    }

}