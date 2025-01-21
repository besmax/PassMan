package bes.max.features.main.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bes.max.features.main.domain.repositories.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val isNighModeActive = settingsRepository.isNightModeActive()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = false
        )

    val isPinCodeUsed = settingsRepository.pinCodeIsUsed()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = false
        )

    fun toggleDarkMode(isActive: Boolean) {
        viewModelScope.launch {
            settingsRepository.setIsNightModeActive(isActive)
        }
    }

}