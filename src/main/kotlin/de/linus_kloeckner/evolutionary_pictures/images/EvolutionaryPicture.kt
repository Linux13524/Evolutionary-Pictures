package de.linus_kloeckner.evolutionary_pictures.images

import javafx.scene.image.PixelReader
import kotlin.random.Random

class EvolutionaryPicture : Picture {

    constructor(size: Size) : super(size)
    constructor(width: Int, height: Int) : super(width, height)
    constructor(pixelReader: PixelReader, width: Int, height: Int) : super(pixelReader, width, height)

    override fun copy() = EvolutionaryPicture(pixelReader, getIntWidth(), getIntHeight())

    fun init() {
        fullIteration { x, y ->
            val r = Random.nextDouble()
            val g = Random.nextDouble()
            val b = Random.nextDouble()
            this.setPixel(Pixel(x, y, r, g, b))
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
                this.setPixel(Pixel.random(x, y))
            }
        }
    }

}