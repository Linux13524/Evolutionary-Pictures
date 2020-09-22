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
    inner class Initialization {

        @Test
        fun `check copy produces same picture`() {
            val picture1 = EvolutionaryPicture(100, 100).apply { init() }
            val picture2 = picture1.copy()

            assertEquals(picture1, picture2)
        }

        @Test
        fun `check init does change picture`() {
            val picture1 = EvolutionaryPicture(100, 100)
            val picture2 = picture1.copy().init()

            assertNotEquals(picture1, picture2)
        }
    }

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

        @Test
        fun `check mutation changes picture`() {
            val picture1 = EvolutionaryPicture(100, 100).apply { fill(Color.RED) }
            val picture2 = picture1.copy()

            picture1.mutate(1.0)

            assertNotEquals(picture1, picture2)
        }

        @Test
        fun `check mutation not changes picture`() {
            val picture1 = EvolutionaryPicture(100, 100).apply { fill(Color.RED) }
            val picture2 = picture1.copy()

            picture1.mutate(0.0)

            assertEquals(picture1, picture2)
        }

    }

}