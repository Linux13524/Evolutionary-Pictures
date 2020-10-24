package de.linus_kloeckner.evolutionary_pictures.algorithm

import de.linus_kloeckner.evolutionary_pictures.images.Picture
import de.linus_kloeckner.evolutionary_pictures.images.Pixel
import de.linus_kloeckner.evolutionary_pictures.utils.distance
import javafx.scene.paint.Color

object ColorPicker {
    fun calculateColors(picture: Picture, colorTolerance: Int = 5): List<Color> {
        val colorDistance = (100 - colorTolerance) / 100.0
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
                dist < colorDistance || dist == 1.0
            }
            i++
        }

        return grouped.keys.toList()
    }

    fun pick(picture: Picture, number: Int, colorTolerance: Int = 5): List<Color> {
        val colors = calculateColors(picture, colorTolerance)

        return if (colors.size <= number) colors
        else colors.subList(0, number)
    }
}