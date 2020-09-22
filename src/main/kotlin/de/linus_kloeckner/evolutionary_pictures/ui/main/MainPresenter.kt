package de.linus_kloeckner.evolutionary_pictures.ui.main

import de.linus_kloeckner.evolutionary_pictures.images.EvolutionaryPicture
import de.linus_kloeckner.evolutionary_pictures.images.Picture
import de.linus_kloeckner.evolutionary_pictures.ui.dialogs.intDialog
import de.linus_kloeckner.evolutionary_pictures.utils.percentage
import javafx.beans.property.*
import javafx.scene.control.Alert
import javafx.scene.image.Image
import javafx.stage.FileChooser
import tornadofx.*

class MainPresenter : Controller() {

    private val MUTATION_RATE = 0.1.percentage()

    private var inputPicturePath: String? = null
    private var inputPicture: Picture? = null
    val inputPictureSizeProperty = SimpleStringProperty()
    val inputImageProperty = SimpleObjectProperty<Image>()

    private var picturePixelSize = 1
    private var outputPicture: EvolutionaryPicture? = null
    val outputPictureSizeProperty = SimpleStringProperty()
    val outputImageProperty = SimpleObjectProperty<Image>()

    fun openPicture() {
        val files = chooseFile("Open Picture", arrayOf(FileChooser.ExtensionFilter("Picture", "*.jpg", "*.png")))
        inputPicturePath = files.firstOrNull()?.absolutePath

        inputPicture = inputPicturePath?.let { Picture.loadFromFilesystem(it) }
        inputImageProperty.value = inputPicture
        inputPictureSizeProperty.value = inputPicture?.getSize().toString()

        outputPicture = null
        outputPictureSizeProperty.set(null)
        outputImageProperty.set(null)
    }

    fun setOutputPixelSize() {
        // Set pixel size
        val inputPicture = inputPicture
        if (inputPicture == null) {
            alert(Alert.AlertType.WARNING, "Before setting a new output pixel size you have to set an input picture.")
            return
        }

        val picturePixelSize = intDialog("New output pixel size", "New Size:", 1)

        if (inputPicture.getIntWidth() % picturePixelSize != 0 || inputPicture.getIntHeight() % picturePixelSize != 0)
            alert(Alert.AlertType.WARNING, "Input picture size is not dividable by $picturePixelSize!").also { return }

        // Calculate picture size
        val newPictureSize = calculatePictureSize(inputPicture)

        // Set input picture size based on pixel size
        this.inputPicture = inputPicturePath?.let { Picture.loadFromFilesystem(it, newPictureSize) }

        // Set output picture size based on pixel size
        outputPicture = EvolutionaryPicture(newPictureSize).also { it.init() }

        setOutputPictureBinding()
    }

    private fun calculatePictureSize(inputPicture: Picture): Picture.Size {
        return Picture.Size(
                inputPicture.getIntWidth() / picturePixelSize,
                inputPicture.getIntHeight() / picturePixelSize)
    }

    private fun setOutputPictureBinding() {
        val outputPicture = outputPicture ?: return

        outputPictureSizeProperty.value = outputPicture.getSize().toString()
        outputImageProperty.value = outputPicture.upsample(picturePixelSize)
    }

    var stopLoopProperty = SimpleBooleanProperty(true)
    fun startMainLoop() {
        runAsync {
            stopLoopProperty.value = false
            while (!stopLoopProperty.value) {
                val newPicture = EvolutionaryPicture(outputPicture!!)

                newPicture.mutate(MUTATION_RATE)
                val newMatch = newPicture.match(inputPicture!!)
                if (newMatch > matchProperty.get()) {
                    runLater { matchProperty.set(newMatch) }

                    outputPicture = newPicture
                    if (counterProperty.get() % 1000 == 0)
                        runLater { setOutputPictureBinding() }
                }
                runLater { counterProperty.value++ }
            }
        }

    }

    fun stopMainLoop() {
        stopLoopProperty.value = true
    }

}