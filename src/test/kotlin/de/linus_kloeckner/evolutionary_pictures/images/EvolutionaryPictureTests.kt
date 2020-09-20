package de.linus_kloeckner.evolutionary_pictures.images

import javafx.scene.paint.Color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

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

        @ParameterizedTest
        @CsvSource(
                "1.0, 1.0, true",
                "1.0, 0.5, true",
                "1.0, 0.0, false",
                "0.0, 0.0, false",
                "0.0, 1.0, false"
        )
        fun `check mutation changes picture`(mutationProbability: Double, mutationValue: Double, shouldChange: Boolean) {
            val picture1 = EvolutionaryPicture(100, 100).apply { fill(Color.RED) }
            val picture2 = Picture(picture1)

            picture1.mutate(mutationProbability, mutationValue)

            if (shouldChange)
                assertNotEquals(picture1, picture2)
            else
                assertEquals(picture1, picture2)
        }

    }

}