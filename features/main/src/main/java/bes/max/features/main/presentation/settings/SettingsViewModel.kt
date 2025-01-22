package bes.max.features.main.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bes.max.cipher.api.CipherApi
import bes.max.features.main.domain.models.PinCodeModelMain
import bes.max.features.main.domain.repositories.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PIN_CODE_ALIAS = "pin_code_alias"

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val cipher: CipherApi,
) : ViewModel() {

    val isNighModeActive = settingsRepository.isNightModeActive()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = false
        )

    val pinCode = settingsRepository.pinCode()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )

    private val _event = MutableLiveData<SettingsEvent>(SettingsEvent.Default)
    val event: LiveData<SettingsEvent> = _event

    fun toggleDarkMode(isActive: Boolean) {
        viewModelScope.launch {
            settingsRepository.setIsNightModeActive(isActive)
        }
    }

    fun togglePinCodeUsing(use: Boolean) {
        viewModelScope.launch {
            if (use) {
                _event.postValue(SettingsEvent.TurnOnPinCode(::resetEvent, ::checkPinCode))
            } else {
                settingsRepository.resetPinCode()
            }
        }
    }

    fun checkPinCode(pinCode: String) {
        _event.postValue(
            SettingsEvent.ReCheckPinCode(
                pinCode = pinCode,
                onSuccess = {
                    savePinCode(pinCode)
                    resetEvent()
                },
                resetEvent = ::resetEvent
            )
        )
    }

    private fun savePinCode(pinCode: String) {
        viewModelScope.launch {
            val encrypted = cipher.encrypt(PIN_CODE_ALIAS, pinCode)
            settingsRepository.setPinCode(
                PinCodeModelMain(
                    active = true,
                    pinCode = encrypted.encryptedData,
                    iv = encrypted.passwordIv
                )
            )
        }
    }

    fun showPinCode(): String {
        val current = pinCode.value
        return if (current != null) {
            cipher.decrypt(
                alias = PIN_CODE_ALIAS,
                encryptedData = current.pinCode,
                initVector = current.iv
            )
        } else {
            ""
        }
    }

    fun checkInputPinCode(input: String): Boolean {
        val current = pinCode.value
         val pinCode = if (current != null) {
            cipher.decrypt(
                alias = PIN_CODE_ALIAS,
                encryptedData = current.pinCode,
                initVector = current.iv
            )
        } else {
            ""
        }
        return pinCode == input
    }

    fun resetEvent() {
        _event.postValue(SettingsEvent.Default)
    }

}