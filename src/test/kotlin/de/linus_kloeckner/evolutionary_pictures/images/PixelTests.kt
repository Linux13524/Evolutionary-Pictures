package de.linus_kloeckner.evolutionary_pictures.images

import ColorConverter
import javafx.scene.paint.Color
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.converter.ConvertWith
import org.junit.jupiter.params.provider.CsvSource
import java.lang.IllegalArgumentException

@Nested
class PixelTests {

    @Nested
    inner class Initialization {

        @ParameterizedTest
        @CsvSource(
                "0.0, 0.5, 1.1",
                "-1.0, 0.5, 1.0"
        )
        fun `check input value boundaries`(r: Double, g: Double, b: Double) {
            assertThrows<IllegalArgumentException> { Pixel(0, 0, r, g, b) }
        }

        @ParameterizedTest
        @CsvSource(
                "black, 0.0, 0.0, 0.0",
                "white, 1.0, 1.0, 1.0",
                "darkgray, 0.6627451f, 0.6627451f, 0.6627451f"
        )
        fun `check color convert`(@ConvertWith(ColorConverter::class) color: Color,
                                  expectedR: Double,
                                  expectedG: Double,
                                  expectedB: Double) {

            val pixel = Pixel(0, 0, color)

            Assertions.assertArrayEquals(
                    arrayOf(expectedR, expectedG, expectedB).toDoubleArray(),
                    arrayOf(pixel.r(), pixel.g(), pixel.b()).toDoubleArray(), 0.1 / 100)
        }

    }

    @Nested
    inner class Match {

        @ParameterizedTest
        @CsvSource(
                "black, black, 1.0",
                "white, white, 1.0",
                "black, white, 0.0",
                "darkgray, white, 0.6627451"
        )
        fun `check match`(@ConvertWith(ColorConverter::class) color1: Color,
                          @ConvertWith(ColorConverter::class) color2: Color,
                          expectedMatch: Double) {

            val pixel1 = Pixel(0, 0, color1)
            val pixel2 = Pixel(0, 0, color2)

            val match = pixel1.match(pixel2)

            Assertions.assertEquals(expectedMatch, match, 0.1 / 100)
        }
    }

    @Nested
    inner class Crossover {

        @ParameterizedTest
        @CsvSource("1", "2")
        fun `check crossover on same pixel`(splice: Int) {
            val pixel1 = Pixel(0, 0, Color.BLACK)

            Assertions.assertEquals(pixel1, pixel1.crossover(pixel1, splice).first)
            Assertions.assertEquals(pixel1, pixel1.crossover(pixel1, splice).second)
        }

        @ParameterizedTest
        @CsvSource("1", "2")
        fun `check crossover on black white pixels`(splice: Int) {
            val pixel1 = Pixel(0, 0, Color.BLACK)
            val pixel2 = Pixel(0, 0, Color.WHITE)

            if (splice == 1) {
                Assertions.assertEquals(Pixel(0, 0, 0.0, 1.0, 1.0), pixel1.crossover(pixel2, splice).first)
                Assertions.assertEquals(Pixel(0, 0, 1.0, 0.0, 0.0), pixel1.crossover(pixel2, splice).second)
            } else {
                Assertions.assertEquals(Pixel(0, 0, 0.0, 0.0, 1.0), pixel1.crossover(pixel2, splice).first)
                Assertions.assertEquals(Pixel(0, 0, 1.0, 1.0, 0.0), pixel1.crossover(pixel2, splice).second)
            }
        }
    }
}