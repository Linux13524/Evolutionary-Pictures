package de.linus_kloeckner.evolutionary_pictures.algorithm

import de.linus_kloeckner.evolutionary_pictures.images.Picture
import de.linus_kloeckner.evolutionary_pictures.images.Pixel
import de.linus_kloeckner.evolutionary_pictures.utils.distance
import javafx.scene.paint.Color

private val COLOR_DISTANCE = 0.99

object ColorPicker {
    fun pick(number: Int, picture: Picture): List<Color> {
        val pixels = mutableListOf<Pixel>()
        picture.fullIteration { x, y ->
            pixels.add(picture.getPixel(x, y))
        }
        var grouped = pixels.groupBy { it.color }.toList().sortedByDescending { (_, pixels) -> pixels.count() }.toMap()

        var i = 0
        while (i < grouped.size) {
            val curColor = grouped.keys.toList()[i]
            grouped = grouped.filter {
                val dist = curColor.distance(it.key)
                dist < COLOR_DISTANCE || dist == 1.0
            }
            i++
        }

        return if (grouped.size <= number) grouped.keys.toList()
        else grouped.keys.toList().subList(0, number)
    }
}