package bes.max.features.main.presentation.settings

sealed interface SettingsEvent {

    data object TurnOnPinCode : SettingsEvent

    data class ReCheckPinCode(val pinCode: String) : SettingsEvent
}
