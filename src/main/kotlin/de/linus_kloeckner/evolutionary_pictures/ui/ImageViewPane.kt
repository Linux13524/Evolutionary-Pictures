package de.linus_kloeckner.evolutionary_pictures.ui

import javafx.event.EventTarget
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import tornadofx.*


object ImageViewPane {
    fun EventTarget.imageviewpane(op: ImageView.() -> Unit = {}): Pane {
        val pane = Pane()
        val iv = ImageView()
        this.addChildIfPossible(pane)
        pane.addChildIfPossible(iv)
        iv.isPreserveRatio = true
        iv.fitWidthProperty().bind(pane.widthProperty())
        iv.fitHeightProperty().bind(pane.heightProperty())
        op(iv)
        return pane
    }
}
