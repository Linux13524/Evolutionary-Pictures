package de.linus_kloeckner.evolutionary_pictures.images

import javafx.scene.image.Image
import javafx.scene.image.PixelReader
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import java.io.File

open class Picture : WritableImage {

    data class Size(val width: Int, val height: Int) {
        override fun toString(): String = "$width x $height"
    }

    constructor(size: Size) : super(size.width, size.height)
    constructor(width: Int, height: Int) : super(width, height)

    constructor(pixelReader: PixelReader, width: Int, height: Int) : super(pixelReader, width, height)

    companion object {
        fun loadFromFilesystem(path: String): EvolutionaryPicture {
            val stream = File(path).inputStream()
            val image = Image(stream)
            return EvolutionaryPicture(image.pixelReader, image.width.toInt(), image.height.toInt())
        }

        fun loadFromFilesystem(path: String, size: Size): EvolutionaryPicture {
            val stream = File(path).inputStream()
            val image = Image(stream, size.width.toDouble(), size.height.toDouble(), false, false)
            return EvolutionaryPicture(image.pixelReader, image.width.toInt(), image.height.toInt())
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Picture) return false
        var isEqual = true
        fullIteration { x, y ->
            if (getPixel(x, y) != other.getPixel(x, y)) {
                isEqual = false
                return@fullIteration
            }
        }
        return isEqual
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    open fun copy() = Picture(this.pixelReader, this.getIntWidth(), this.getIntHeight())

    fun getIntWidth(): Int = this.width.toInt()
    fun getIntHeight(): Int = this.height.toInt()
    fun getSize(): Size = Size(getIntWidth(), getIntHeight())

    fun fullIteration(action: (Int, Int) -> Unit) =
            (0 until getIntWidth()).forEach { x ->
                (0 until getIntHeight()).forEach { y -> action(x, y) }
            }

    fun getPixel(x: Int, y: Int) = Pixel(x, y, pixelReader.getColor(x, y))
    fun setPixel(pixel: Pixel) = pixelWriter.setColor(pixel.x, pixel.y, pixel.color)

    fun match(other: Picture): Double {
        check(getSize() == other.getSize()) { "Sizes of pictures do not match: ${getSize()} <> ${other.getSize()}" }

        var matchAcc = 0.0
        fullIteration { x, y ->
            val match = getPixel(x, y).match(other.getPixel(x, y))
            matchAcc += match
        }

        return matchAcc / getIntHeight() * getIntWidth() / 100
    }

    fun fill(color: Color) {
        fullIteration { x, y ->
            pixelWriter.setColor(x, y, color)
        }
    }

    fun upsample(scaleFactor: Int): Picture {
        val output = Picture(getIntWidth() * scaleFactor, getIntHeight() * scaleFactor)

        val reader = this.pixelReader
        val writer = output.pixelWriter

        fullIteration { x, y ->
            val argb = reader.getArgb(x, y)
            for (dy in 0 until scaleFactor) {
                for (dx in 0 until scaleFactor) {
                    writer.setArgb(x * scaleFactor + dx, y * scaleFactor + dy, argb)
                }
            }
        }

        return output
    }
}