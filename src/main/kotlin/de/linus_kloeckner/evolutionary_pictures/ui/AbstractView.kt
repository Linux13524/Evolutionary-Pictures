package de.linus_kloeckner.evolutionary_pictures.ui

import tornadofx.*

abstract class AbstractView<Presenter : Controller> : View() {
    abstract val presenter: Presenter
}