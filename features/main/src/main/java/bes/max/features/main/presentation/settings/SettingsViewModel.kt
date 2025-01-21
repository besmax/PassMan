package bes.max.features.main.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bes.max.cipher.api.CipherApi
import bes.max.features.main.domain.models.SiteInfoModelMain
import bes.max.features.main.domain.repositories.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    fun toggleDarkMode(isActive: Boolean) {
        viewModelScope.launch {
            settingsRepository.setIsNightModeActive(isActive)
        }
    }

    fun togglePinCodeUsing(use: Boolean) {
        viewModelScope.launch {
            // TODO
        }
    }

    fun showPinCode(pinCode: Int): String {
        return cipher.decrypt(
            alias = model.name,
            encryptedData = model.password,
            initVector = model.passwordIv
        )
    }

}