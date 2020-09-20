package de.linus_kloeckner.evolutionary_pictures.images

import javafx.scene.paint.Color

object Canvas {

    fun drawSquare(pic: Picture, x: Int, y: Int, size: Int, color: Color) {
        require(x in 0 until pic.getIntWidth())
        require(y in 0 until pic.getIntHeight())

        val radius = size / 2

        for (ix in (x - radius)..(x + radius)) {
            for (iy in (y - radius)..(y + radius)) {
                if (ix in 0 until pic.getIntWidth() && iy in 0 until pic.getIntHeight())
                    pic.setPixel(Pixel(ix, iy, color))
            }
        }
    }

}