package de.linus_kloeckner.evolutionary_pictures.algorithm

import de.linus_kloeckner.evolutionary_pictures.images.EvolutionaryPicture
import de.linus_kloeckner.evolutionary_pictures.images.Picture
import de.linus_kloeckner.evolutionary_pictures.utils.percentage
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import tornadofx.*

private val MUTATION_RATE = 0.1.percentage()

class EvolutionaryAlgorithm(private val properties: Properties, private val settings: Settings) {

    data class Settings(val populationSize: Int,
                        val pictureSize: Picture.Size,
                        val picturePixelSize: Int,
                        val targetPicture: Picture)

    data class Properties(val bestMatchProperty: SimpleDoubleProperty,
                          val currentGenerationProperty: SimpleIntegerProperty,
                          val stopLoopProperty: SimpleBooleanProperty,
                          val outputImageProperty: SimpleObjectProperty<Image>)

    class Individual(private val settings: Settings, copyPicture: EvolutionaryPicture? = null) {
        val picture: EvolutionaryPicture =
                if (copyPicture == null) EvolutionaryPicture(settings.pictureSize).apply { init() }
                else EvolutionaryPicture(copyPicture)
        var match = 0.0
            private set

        init {
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

    private var population: List<Individual> = List(settings.populationSize) { Individual(settings) }

    fun startEvolution() {
        runAsync {
            properties.stopLoopProperty.value = false
            while (!properties.stopLoopProperty.value) {

                population = population.map { individual ->
                    val newIndividual = individual.copy().also { it.mutate() }

                    if (newIndividual.match > individual.match)
                        newIndividual
                    else
                        individual
                }

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
    }

    fun stopEvolution() {
        properties.stopLoopProperty.value = true
    }

}