package de.linus_kloeckner.evolutionary_pictures.images

import javafx.scene.paint.Color
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import java.lang.IllegalArgumentException

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CanvasTest {

    @Nested
    inner class Square {

        @Test
        fun `check boundaries`() {
            val size = 200
            val color = Color.BLACK
            val pic = EvolutionaryPicture(size, size)

            assertThrows<IllegalArgumentException> { Canvas.drawSquare(pic, 300, 100, 5, color) }
            assertThrows<IllegalArgumentException> { Canvas.drawSquare(pic, 100, 300, 5, color) }
            assertThrows<IllegalArgumentException> { Canvas.drawSquare(pic, size, size, 5, color) }
            assertDoesNotThrow { Canvas.drawSquare(pic, size - 1, size - 1, 5, color) }
        }

        @Test
        fun `full draw`() {

            val pic = EvolutionaryPicture(101, 101).apply { fill(Color.WHITE) }

            Canvas.drawSquare(pic, 50, 50, 100, Color.BLACK)

            pic.fullIteration { x, y ->
                assertEquals(Color.BLACK, pic.getPixel(x, y).getColor())
            }
        }

    }


}