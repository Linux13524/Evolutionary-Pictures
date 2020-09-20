package de.linus_kloeckner.evolutionary_pictures.images

import javafx.scene.paint.Color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EvolutionaryPictureTests {

    @Nested
    inner class Evolutions {

        @Test
        fun `check crossover on same picture`() {
            val picture = EvolutionaryPicture(100, 100)

            assertEquals(picture, picture.crossover(picture).first)
            assertEquals(picture, picture.crossover(picture).second)
        }

        @Test
        fun `check crossover on black white pictures`() {
            val picture1 = EvolutionaryPicture(100, 100).apply { fill(Color.BLACK) }
            val picture2 = EvolutionaryPicture(100, 100).apply { fill(Color.WHITE) }

            assertNotEquals(picture1, picture1.crossover(picture2).first)
            assertNotEquals(picture2, picture1.crossover(picture2).first)
            assertNotEquals(picture1, picture1.crossover(picture2).second)
            assertNotEquals(picture2, picture1.crossover(picture2).second)
        }

    }

}