package de.linus_kloeckner.evolutionary_pictures.ui.main

import de.linus_kloeckner.evolutionary_pictures.ui.AbstractView
import de.linus_kloeckner.evolutionary_pictures.ui.ImageViewPane.imageviewpane
import de.linus_kloeckner.evolutionary_pictures.ui.Styles.Companion.container
import de.linus_kloeckner.evolutionary_pictures.utils.notNull
import javafx.beans.property.SimpleBooleanProperty
import tornadofx.*

class MainView : AbstractView<MainPresenter>() {

    override val presenter: MainPresenter by inject()

    private val showInputProperty = SimpleBooleanProperty(false)

    init {
        primaryStage.isMaximized = true
    }

    override val root = borderpane {

        left = vbox(8) {
            addClass(container)

            button("Open Picture") {
                enableWhen(presenter.algorithmProperties.stopLoopProperty)
                action {
                    presenter.openPicture()
                }
            }

            button("Set output pixel size") {
                enableWhen(presenter.algorithmProperties.stopLoopProperty and presenter.inputImageProperty.notNull())
                action {
                    presenter.setOutputPixelSize()
                }
            }

            button("Start algorithm") {
                enableWhen(presenter.algorithmProperties.stopLoopProperty and presenter.outputPictureSizeProperty.notNull())
                action {
                    presenter.startMainLoop()
                }
            }

            button("Stop algorithm") {
                enableWhen(!presenter.algorithmProperties.stopLoopProperty)
                action {
                    presenter.stopMainLoop()
                }
            }
        }

        center = vbox(8) {
            addClass(container)

            button("Show Evolutionary Picture") {
                action {
                    showInputProperty.value = !showInputProperty.value
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
                        imageProperty().bind(presenter.algorithmProperties.outputImageProperty)
                        isSmooth = false
                    }
                }
            }
        }

        bottom = hbox(8) {
            label("Best Match: ")
            label {
                bind(presenter.algorithmProperties.bestMatchProperty)
            }

            label("Avg Match: ")
            label {
                bind(presenter.algorithmProperties.avgMatchProperty)
            }

            label("Worst Match: ")
            label {
                bind(presenter.algorithmProperties.worstMatchProperty)
            }

            label("Picture: ")
            label {
                bind(presenter.algorithmProperties.currentGenerationProperty)
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



