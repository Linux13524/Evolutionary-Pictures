package de.linus_kloeckner.evolutionary_pictures.ui.main

import de.linus_kloeckner.evolutionary_pictures.ui.AbstractView
import de.linus_kloeckner.evolutionary_pictures.ui.ImageViewPane.imageviewpane
import de.linus_kloeckner.evolutionary_pictures.ui.Styles.Companion.container
import javafx.beans.property.SimpleBooleanProperty
import tornadofx.*

class MainView : AbstractView<MainPresenter>() {

    init {
        primaryStage.isMaximized = true
    }

    override val presenter: MainPresenter by inject()

    private val showInputProperty = SimpleBooleanProperty(true)

    override val root = borderpane {

        left = vbox(8) {
            addClass(container)

            button("Open Picture") {
                action {
                    presenter.openPicture()
                }
            }

            button("Set output pixel size") {
                action {
                    presenter.setNewOutputPicture()
                }
            }
        }

        center = vbox(8) {
            addClass(container)

            button("Show Evolutionary Picture") {
                action {
                    showInputProperty.set(!showInputProperty.value)
                    text = if (showInputProperty.value) "Show Evolutionary Picture" else "Show Input Picture"
                }
            }

            stackpane {

                vbox {
                    visibleWhen(showInputProperty)

                    label("Input Picture:")

                    imageviewpane {
                        imageProperty().bind(presenter.inputImageProperty)
                        isSmooth = false
                    }
                }

                vbox {
                    hiddenWhen(showInputProperty)

                    label("Output Picture:")

                    imageviewpane {
                        imageProperty().bind(presenter.outputImageProperty)
                        isSmooth = false
                    }
                }
            }
        }

        right = vbox(8) {
            addClass(container)

            gridpane {
                row {
                    label("Input Picture Size:")
                    label().textProperty().bind(presenter.inputPictureSizeProperty)
                }
                row {
                    label("Output Picture Size:")
                    label().textProperty().bind(presenter.outputPictureSizeProperty)
                }
            }
        }
    }
}



