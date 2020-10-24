package de.linus_kloeckner.evolutionary_pictures.ui.main

import de.linus_kloeckner.evolutionary_pictures.algorithm.EvolutionaryAlgorithm
import de.linus_kloeckner.evolutionary_pictures.images.Picture
import de.linus_kloeckner.evolutionary_pictures.ui.dialogs.colorPickerDialog
import de.linus_kloeckner.evolutionary_pictures.ui.dialogs.intDialog
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Alert
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import tornadofx.Controller
import tornadofx.alert
import tornadofx.chooseFile

class MainPresenter : Controller() {

    private var inputPicturePath: String? = null

    val inputImageProperty = SimpleObjectProperty<Image>()

    private var picturePixelSize = 1
    private var pictureSize: Picture.Size? = null
    private var pictureColorPalette: List<Color>? = null

    val inputPictureSizeProperty = SimpleStringProperty()
    val outputPictureSizeProperty = SimpleStringProperty()

    fun openPicture() {
        val files = chooseFile("Open Picture", arrayOf(FileChooser.ExtensionFilter("Picture", "*.jpg", "*.png")))
        inputPicturePath = files.firstOrNull()?.absolutePath

        val inputPicture = inputPicturePath?.let { Picture.loadFromFilesystem(it) } ?: return
        inputImageProperty.value = inputPicture
        inputPictureSizeProperty.value = inputPicture.getSize().toString()

        outputPictureSizeProperty.value = null
        algorithmProperties.outputImageProperty.value = null
    }

    fun setOutputPixelSize() {
        // Set pixel size
        val inputPicture = inputPicturePath?.let { Picture.loadFromFilesystem(it) }
        if (inputPicture == null) {
            alert(Alert.AlertType.WARNING, "Before setting a new output pixel size you have to set an input picture.")
            return
        }

        picturePixelSize = intDialog("New output pixel size", "New Size:", 1)

        if (inputPicture.getIntWidth() % picturePixelSize != 0 || inputPicture.getIntHeight() % picturePixelSize != 0)
            alert(Alert.AlertType.WARNING, "Input picture size is not dividable by $picturePixelSize!").also { return }

        // Calculate picture size
        pictureSize = Picture.Size(
                inputPicture.getIntWidth() / picturePixelSize,
                inputPicture.getIntHeight() / picturePixelSize)

        outputPictureSizeProperty.value = pictureSize?.toString()
    }

    val algorithmProperties = EvolutionaryAlgorithm.Properties()

    private var algorithmInstance: EvolutionaryAlgorithm? = null
    private fun initEvolutionaryAlgorithm() {
        val pictureSize = pictureSize ?: return
        val inputPicture = inputPicturePath?.let { Picture.loadFromFilesystem(it, pictureSize) } ?: return

        val settings = EvolutionaryAlgorithm.Settings(pictureSize, picturePixelSize, inputPicture, pictureColorPalette)
        algorithmInstance = EvolutionaryAlgorithm(algorithmProperties, settings)
    }

    fun startAlgorithm() {
        initEvolutionaryAlgorithm()

        startMainLoop()
    }

    fun stopAlgorithm() {
        algorithmProperties.currentGenerationProperty.value = 0
        algorithmInstance = null
    }

    fun startMainLoop() {
        algorithmInstance?.startEvolution()
    }

    fun stopMainLoop() {
        algorithmInstance?.stopEvolution()
    }

    fun pickColor() {
        val inputPicture = inputPicturePath?.let { Picture.loadFromFilesystem(it) } ?: return
        pictureColorPalette = colorPickerDialog(inputPicture)
    }
}