package de.linus_kloeckner.evolutionary_pictures.ui

import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val container by cssclass()
    }

    init {
        container {
            padding = box(15.px)
        }

        label {
            padding = box(8.px)
        }
    }
}