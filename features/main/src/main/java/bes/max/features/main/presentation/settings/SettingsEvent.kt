package bes.max.features.main.presentation.settings

import bes.max.passman.features.main.R

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

    data class NoAppForSharing(
        val messageResId: Int = R.string.no_app_for_share_file,
        override val resetEvent: () -> Unit
    ) : SettingsEvent
}
