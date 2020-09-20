package de.linus_kloeckner.evolutionary_pictures.images

import com.nhaarman.mockitokotlin2.UseConstructor.Companion.withArguments
import com.nhaarman.mockitokotlin2.mock
import javafx.scene.paint.Color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PictureTests {

    @Nested
    inner class Initialization {

        @Test
        fun `check input values`() {
            assertThrows<IllegalArgumentException> { Picture(-20, 30) }
            assertThrows<IllegalArgumentException> { Picture(30, 0) }
        }

    }

    @Nested
    inner class Match {

        @ParameterizedTest
        @CsvSource(
                "100, 200, 100, 300",
                "100, 200, 300, 200"
        )
        fun `check for different widths and heights`(width1: Int, height1: Int, width2: Int, height2: Int) {
            val picture1 = Picture(width1, height1)
            val picture2 = Picture(width2, height2)

            assertThrows<IllegalStateException> { picture1.match(picture2)}
        }

        @Test
        fun `check initial picture match`() {
            val picture1 = Picture(100, 100)
            val picture2 = Picture(100, 100)

            assertEquals(100.0, picture1.match(picture2))
        }

        @Test
        fun `check black white match`() {
            val picture1: Picture = mock(useConstructor = withArguments(100, 100))
            val picture2: Picture = mock(useConstructor = withArguments(100, 100))
            picture2.fill(Color.WHITE)

            assertEquals(0.0, picture1.match(picture2))
        }

        @Test
        fun `copied pictures do match`() {
            val picture1 = Picture(100, 100)
            picture1.fill(Color.RED)
            val picture2 = Picture(picture1)

            assertEquals(100.0, picture1.match(picture2))
        }

        @Test
        fun `copied pictures do not match after one was changed`() {
            val picture1 = Picture(100, 100)
            val picture2 = Picture(picture1)
            picture1.fill(Color.WHITE)

            assertEquals(0.0, picture1.match(picture2))
        }
    }

}