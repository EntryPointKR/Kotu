@file:Suppress("EXPERIMENTAL_API_USAGE")

package kr.entree.kotu.ui.wizard

import javafx.beans.property.SimpleStringProperty
import javafx.scene.input.KeyCode
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.suspendCancellableCoroutine
import tornadofx.*

suspend fun awaitTextInput(title: String) = suspendCancellableCoroutine<String> {
    TextWizardView(it, title).openWindow()
}

/**
 * Created by JunHyung Lim on 2020-03-28
 */
class TextWizardView(
    val continuation: CancellableContinuation<String>,
    val fieldText: String
) : View(fieldText) {
    val input = SimpleStringProperty()

    init {
        continuation.invokeOnCancellation { close() }
    }

    fun resumeContinuation() {
        continuation.apply {
            Dispatchers.JavaFx.resumeUndispatched(input.value ?: "")
        }
        close()
    }

    override val root = form {
        fieldset {
            field(fieldText) {
                textfield(input) {
                    setOnKeyPressed {
                        if (it.code == KeyCode.ENTER) {
                            resumeContinuation()
                        }
                    }
                }
                button("OK").setOnAction {
                    resumeContinuation()
                }
            }
        }
    }
}