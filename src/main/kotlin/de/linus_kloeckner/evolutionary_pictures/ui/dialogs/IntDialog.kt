package de.linus_kloeckner.evolutionary_pictures.ui.dialogs

import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.HBox
import tornadofx.*
import java.util.function.UnaryOperator

fun intDialog(title: String, labelText: String, oldInt: Int): Int {

    val dialog = object : Dialog<ButtonType>() {}
    dialog.dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
    dialog.title = title

    val hbox = HBox()
    hbox.alignment = Pos.CENTER
    hbox.spacing = 8.0
    val label = Label(labelText)
    val intField = TextField(oldInt.toString())
    intField.maxWidth = 50.0
    val filter = UnaryOperator<TextFormatter.Change> {
        return@UnaryOperator if (it.text.matches(Regex("[0-9]*"))) it else null
    }
    val formatter = TextFormatter<String>(filter)
    intField.textFormatter = formatter

    hbox.add(label)
    hbox.add(intField)
    dialog.dialogPane.content = hbox
    val buttonClicked = dialog.showAndWait()
    if (buttonClicked.isPresent && buttonClicked.get() == ButtonType.OK) {
        return intField.text.toInt()
    }

    return oldInt
}

