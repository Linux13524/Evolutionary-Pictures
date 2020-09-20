package de.linus_kloeckner.evolutionary_pictures.utils

import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.Property

fun <T> Property<T>.toBoolean(converter: (T) -> Boolean): BooleanBinding = Bindings.createBooleanBinding({ converter(this.value) }, this)
fun <T> Property<T>.notNull(): BooleanBinding = toBoolean { it != null }
