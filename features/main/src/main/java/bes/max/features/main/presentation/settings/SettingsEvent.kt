package bes.max.features.main.presentation.settings

sealed interface SettingsEvent {
    val resetEvent: () -> Unit

    data object Default : SettingsEvent {
        override val resetEvent: () -> Unit
            get() = { }
    }

    data class TurnOnPinCode(
        override val resetEvent: () -> Unit,
        val checkPinCode: (String) -> Unit
    ) : SettingsEvent

    data class ReCheckPinCode(
        val pinCode: String,
        val onSuccess: () -> Unit,
        override val resetEvent: () -> Unit
    ) : SettingsEvent
}
