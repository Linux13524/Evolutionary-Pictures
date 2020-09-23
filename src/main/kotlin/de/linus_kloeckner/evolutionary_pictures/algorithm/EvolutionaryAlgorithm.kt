package de.linus_kloeckner.evolutionary_pictures.algorithm

import de.linus_kloeckner.evolutionary_pictures.images.EvolutionaryPicture
import de.linus_kloeckner.evolutionary_pictures.images.Picture
import de.linus_kloeckner.evolutionary_pictures.utils.map
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
import java.util.*
import kotlin.concurrent.schedule

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

    data class Properties(val bestMatchProperty: SimpleDoubleProperty = SimpleDoubleProperty(),
                          val avgMatchProperty: SimpleDoubleProperty = SimpleDoubleProperty(),
                          val worstMatchProperty: SimpleDoubleProperty = SimpleDoubleProperty(),
                          val currentGenerationProperty: SimpleIntegerProperty = SimpleIntegerProperty(0),
                          val stopLoopProperty: SimpleBooleanProperty = SimpleBooleanProperty(true),
                          val outputImageProperty: SimpleObjectProperty<Image> = SimpleObjectProperty<Image>())

    class Individual(private val settings: Settings, copyPicture: EvolutionaryPicture? = null) {
        val picture: EvolutionaryPicture
        var match = 0.0
            private set

        init {
            picture = if (copyPicture != null) {
                copyPicture.copy()
            } else {
                val newPicture = EvolutionaryPicture(settings.pictureSize)
                newPicture.init(settings.colorPalette)
                newPicture
            }


            match = picture.match(settings.targetPicture)
        }

        fun copy(): Individual {
            return Individual(settings, picture)
        }

        fun mutate() {
            picture.mutate(MUTATION_RATE, settings.colorPalette)
            match = picture.match(settings.targetPicture)
        }
    }

    private var currentGeneration = 0

    private var population: List<Individual> = List(POPULATION_SIZE) { Individual(settings) }

    fun startEvolution() = scope.launch {
        assert(POPULATION_SIZE / THREADS == SUB_POPULATION_SIZE)

        val timer = Timer().schedule(0, 500L) {
            runLater {
                val currentBestPicture = population.maxBy { it.match }!!
                val currentAvgMatch = population.map { it.match }.average()
                val currentWorstPicture = population.minBy { it.match }!!
                properties.bestMatchProperty.value = currentBestPicture.match
                properties.avgMatchProperty.value = currentAvgMatch
                properties.worstMatchProperty.value = currentWorstPicture.match
                properties.currentGenerationProperty.value = currentGeneration
                properties.outputImageProperty.value = currentBestPicture.picture.upsample(settings.picturePixelSize)
            }
        }

        properties.stopLoopProperty.value = false
        while (!properties.stopLoopProperty.value) {

            val results = (0 until THREADS).map {
                val subPopulation = population.shuffled().subList(it * SUB_POPULATION_SIZE, (it + 1) * SUB_POPULATION_SIZE)
                runEvolutionAsync(subPopulation)
            }

            population = results.flatMap { it.await() }

            population = selection(population)

            currentGeneration++
        }
        timer.cancel()
    }

    private fun runEvolutionAsync(subPopulation: List<Individual>) = scope.async {

        val newSubPopulation = mutableListOf<Individual>()
        newSubPopulation.addAll(subPopulation)

        forParents(subPopulation) { parents ->
            val children = crossover(parents).map { mutate(it) }
            newSubPopulation.addAll(children.toList())
        }

        return@async newSubPopulation
    }

    private fun forParents(subPopulation: List<Individual>, apply: (Pair<Individual, Individual>) -> Unit) {
        (0 until (subPopulation.size / 2)).map { subPopulation[it * 2] to subPopulation[it * 2 + 1] }.forEach {
            apply(it)
        }
    }

    private fun crossover(parents: Pair<Individual, Individual>): Pair<Individual, Individual> {
        return parents.first.picture.fullPixelCrossover(parents.second.picture).map { Individual(settings, it) }
    }

    private fun mutate(individual: Individual): Individual {
        val newIndividual = individual.copy().also { it.mutate() }

        return if (newIndividual.match > individual.match) newIndividual
        else individual
    }

    private fun selection(population: List<Individual>): List<Individual> {
        return population.sortedByDescending { it.match }.subList(0, POPULATION_SIZE)
    }

    fun stopEvolution() {
        properties.stopLoopProperty.value = true
    }

}