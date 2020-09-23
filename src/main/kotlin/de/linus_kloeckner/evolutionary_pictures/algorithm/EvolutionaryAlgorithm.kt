package de.linus_kloeckner.evolutionary_pictures.algorithm

import de.linus_kloeckner.evolutionary_pictures.images.EvolutionaryPicture
import de.linus_kloeckner.evolutionary_pictures.images.Picture
import de.linus_kloeckner.evolutionary_pictures.utils.percentage
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import javafx.scene.paint.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import tornadofx.*

private val MUTATION_RATE = 0.1.percentage()
private val POPULATION_SIZE = 160
private val SUB_POPULATION_SIZE = 20
private val THREADS = 8

class EvolutionaryAlgorithm(private val properties: Properties, private val settings: Settings) {

    private val scope = CoroutineScope(Dispatchers.Default)

    data class Settings(val pictureSize: Picture.Size,
                        val picturePixelSize: Int,
                        val targetPicture: Picture,
                        val colorPalette: List<Color>? = null)

    data class Properties(val bestMatchProperty: SimpleDoubleProperty,
                          val currentGenerationProperty: SimpleIntegerProperty,
                          val stopLoopProperty: SimpleBooleanProperty,
                          val outputImageProperty: SimpleObjectProperty<Image>)

    class Individual(private val settings: Settings, copyPicture: EvolutionaryPicture? = null) {
        val picture: EvolutionaryPicture
        var match = 0.0
            private set

        init {
            picture = if (copyPicture != null) {
                copyPicture.copy()
            } else {
                val newPicture = EvolutionaryPicture(settings.pictureSize)
                newPicture.colorPalette = settings.colorPalette
                newPicture.init()
                newPicture
            }


            match = picture.match(settings.targetPicture)
        }

        fun copy(): Individual {
            return Individual(settings, picture)
        }

        fun mutate() {
            picture.mutate(MUTATION_RATE)
            match = picture.match(settings.targetPicture)
        }
    }

    private var currentGeneration = 0

    private var population: List<Individual> = List(POPULATION_SIZE) { Individual(settings) }

    fun startEvolution() = scope.launch {
        assert(POPULATION_SIZE / THREADS == SUB_POPULATION_SIZE)

        properties.stopLoopProperty.value = false
        while (!properties.stopLoopProperty.value) {

            val results = (0 until THREADS).map {
                val subPopulation = population.shuffled().subList(it * SUB_POPULATION_SIZE, (it + 1) * SUB_POPULATION_SIZE)
                runEvolutionAsync(subPopulation)
            }

            population = results.flatMap { it.await() }

            val currentBestPicture = population.maxBy { it.match }!!


            if (currentGeneration % 10 == 0)
                runLater {
                    properties.bestMatchProperty.value = currentBestPicture.match
                    properties.currentGenerationProperty.value = currentGeneration
                    properties.outputImageProperty.value = currentBestPicture.picture.upsample(settings.picturePixelSize)
                }
            currentGeneration++
        }
    }

    private fun runEvolutionAsync(subPopulation: List<Individual>) = scope.async {
        return@async subPopulation.map { individual ->
            val newIndividual = individual.copy().also { it.mutate() }

            if (newIndividual.match > individual.match)
                newIndividual
            else
                individual
        }
    }

    fun stopEvolution() {
        properties.stopLoopProperty.value = true
    }

}