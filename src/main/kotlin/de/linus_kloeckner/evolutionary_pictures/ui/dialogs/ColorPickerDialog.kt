package de.linus_kloeckner.evolutionary_pictures.ui.dialogs

import de.linus_kloeckner.evolutionary_pictures.algorithm.ColorPicker
import de.linus_kloeckner.evolutionary_pictures.images.Picture
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import tornadofx.*
import java.util.function.UnaryOperator

fun colorPickerDialog(picture: Picture, oldColorPalette: List<Color>?): List<Color>? {

    val dialog = object : Dialog<ButtonType>() {}
    dialog.dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
    dialog.title = "Color Picker"

    val intFilter = UnaryOperator<TextFormatter.Change> {
        return@UnaryOperator if (it.text.matches(Regex("[0-9]*"))) it else null
    }

    val label1 = Label("Number of Colors")
    val colorNumberField = TextField("128")
    colorNumberField.maxWidth = 50.0

    val formatter1 = TextFormatter<String>(intFilter)
    colorNumberField.textFormatter = formatter1

    val label2 = Label("Color Distance")
    val colorToleranceField = TextField()
    colorToleranceField.maxWidth = 50.0

    val formatter2 = TextFormatter<String>(intFilter)
    colorToleranceField.textFormatter = formatter2

    val label3 = Label()

    val hbox1 = HBox(8.0).apply {
        alignment = Pos.CENTER
        add(label1)
        add(colorNumberField)
    }

    val hbox2 = HBox(8.0).apply {
        alignment = Pos.CENTER
        add(label2)
        add(colorToleranceField)
    }

    val hbox3 = HBox(8.0).apply {
        alignment = Pos.CENTER
        add(label3)
    }

    dialog.dialogPane.content = VBox(8.0).apply {
        add(hbox1)
        add(hbox2)
        add(hbox3)
    }

    colorToleranceField.textProperty().onChange {
        if(it?.isEmpty != false) {
            label3.text = "Total color count: -"
            return@onChange
        }

        val size = ColorPicker.calculateColors(picture, colorToleranceField.text.toInt()).size
        label3.text = "Total color count: $size"
    }
    colorToleranceField.text = "5"

    val buttonClicked = dialog.showAndWait()

    val number = colorNumberField.text.toInt()
    val colorTolerance = colorToleranceField.text.toInt()

    if (buttonClicked.isPresent && buttonClicked.get() == ButtonType.OK) {
        return ColorPicker.pick(picture, number, colorTolerance)
    }

    return oldColorPalette
}

