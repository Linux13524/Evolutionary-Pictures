package de.linus_kloeckner.evolutionary_pictures.ui.main

import de.linus_kloeckner.evolutionary_pictures.images.EvolutionaryPicture
import de.linus_kloeckner.evolutionary_pictures.images.Picture
import de.linus_kloeckner.evolutionary_pictures.ui.dialogs.intDialog
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Alert
import javafx.scene.image.Image
import javafx.stage.FileChooser
import tornadofx.*

class MainPresenter : Controller() {

    private var inputPicture: Picture? = null
    val inputPictureSizeProperty = SimpleStringProperty()
    val inputImageProperty = SimpleObjectProperty<Image>()

    private var outputPicturePixelSize = 1
    private var outputPicture: EvolutionaryPicture? = null
    val outputPictureSizeProperty = SimpleStringProperty()
    val outputImageProperty = SimpleObjectProperty<Image>()

    fun openPicture() {
        val files = chooseFile("Open Picture", arrayOf(FileChooser.ExtensionFilter("Picture", "*.jpg", "*.png")))

        val newInputPicture = files.firstOrNull()?.absolutePath?.let { Picture.loadFromFilesystem(it) } ?: return

        inputPicture = newInputPicture
        inputPictureSizeProperty.value = newInputPicture.getSize().toString()
        inputImageProperty.value = newInputPicture

        outputPictureSizeProperty.value = newInputPicture.getSize().toString()
        outputImageProperty.value = EvolutionaryPicture(newInputPicture.getSize())
    }

    private fun setOutputPictureBinding() {
        val outputPicture = outputPicture ?: return

        outputPictureSizeProperty.value = outputPicture.getSize().toString()
        outputImageProperty.value = outputPicture.resample(outputPicturePixelSize)
    }

    fun setNewOutputPicture() {
        val inputPicture = inputPicture
        if (inputPicture == null) {
            alert(Alert.AlertType.WARNING, "Before setting a new output pixel size you have to set an input picture.")
            return
        }

        val newPixelSize = intDialog("New output pixel size", "New Size:", outputPicturePixelSize)

        if (inputPicture.getIntWidth() % newPixelSize != 0 || inputPicture.getIntHeight() % newPixelSize != 0)
            alert(Alert.AlertType.WARNING, "Input picture size is not dividable by $newPixelSize!")

        outputPicturePixelSize = newPixelSize

        val newPictureSize = Picture.Size(inputPicture.getIntWidth() / newPixelSize, inputPicture.getIntHeight() / newPixelSize)
        outputPicture = EvolutionaryPicture(newPictureSize).also { it.init() }
        setOutputPictureBinding()
    }

}