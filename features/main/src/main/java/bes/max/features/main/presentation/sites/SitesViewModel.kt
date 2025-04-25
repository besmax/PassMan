package bes.max.features.main.presentation.sites

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bes.max.cipher.api.CipherApi
import bes.max.features.main.domain.models.SiteInfoModelMain
import bes.max.features.main.domain.repositories.CategoriesRepository
import bes.max.features.main.domain.repositories.SettingsRepository
import bes.max.features.main.domain.repositories.SiteInfoRepository
import bes.max.features.main.ui.util.copyTextToClipboard
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SitesViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val siteInfoRepository: SiteInfoRepository,
    private val cipher: CipherApi,
    private val categoriesRepository: CategoriesRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableLiveData<SitesScreenState>(SitesScreenState.Loading)
    val uiState: LiveData<SitesScreenState> = _uiState

    private val _event = MutableLiveData<SitesScreenEvent>()
    val event: LiveData<SitesScreenEvent> = _event

    val isAnimBackgroundActive = settingsRepository.isAnimBackgroundActive()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = false
        )

    fun getSites() {
        _uiState.value = SitesScreenState.Loading
        viewModelScope.launch {
            siteInfoRepository.getAll().collect() { list ->
                if (list.isNotEmpty()) {
                    _uiState.postValue(
                        SitesScreenState.Content(
                            sites = list,
                            filteredSites = list,
                        )
                    )
                    getFilters()
                } else {
                    _uiState.postValue(SitesScreenState.Empty)
                }
            }
        }
    }

    private fun getFilters() {
        viewModelScope.launch {
            val filters = categoriesRepository.getFilters(::filterSites)
            val currentState = uiState.value
            if (currentState is SitesScreenState.Content) {
                _uiState.postValue(currentState.copy(filters = filters.toImmutableList()))
            }
        }
    }

    private fun filterSites(color: Int? = null) {
        val currentState = uiState.value
        if (currentState is SitesScreenState.Content) {
            if (color == null || color == -1) {
                _uiState.postValue(
                    currentState.copy(
                        filteredSites = currentState.sites,
                        selectedCategory = -1
                    )
                )
            } else {
                _uiState.postValue(
                    currentState.copy(
                        filteredSites = currentState.sites.filter { it.categoryColor == color },
                        selectedCategory = currentState.filters.indexOfFirst {
                            it.color.toArgb() == color
                        },
                    )
                )
            }
        }
    }

    fun showPassword(model: SiteInfoModelMain): String {
        return cipher.decrypt(
            alias = model.name,
            encryptedData = model.password,
            initVector = model.passwordIv
        )
    }

    fun copyPasswordToClipboard(model: SiteInfoModelMain) {
        val decryptPassword = cipher.decrypt(
            alias = model.name,
            encryptedData = model.password,
            initVector = model.passwordIv
        )
        appContext.copyTextToClipboard(decryptPassword)
    }

    fun toggleItemSelection(id: Int) {
        val current = _uiState.value
        if (current is SitesScreenState.Content) {
            val filteredSites = current.filteredSites.map {
                if (it.id == id) it.copy(isSelected = !it.isSelected)
                else it
            }
            val sites = current.sites.map {
                if (it.id == id) it.copy(isSelected = !it.isSelected)
                else it
            }
            val selected = sites.count { it.isSelected }
            _uiState.postValue(
                current.copy(
                    filteredSites = filteredSites,
                    sites = sites,
                    selected = selected
                )
            )
        }
    }

    fun unselectAll() {
        val current = _uiState.value
        if (current is SitesScreenState.Content) {
            _uiState.postValue(
                current.copy(
                    sites = current.sites.map { it.copy(isSelected = false) },
                    filteredSites = current.filteredSites.map { it.copy(isSelected = false) },
                    selected = 0
                )
            )
        }
    }

    fun deleteSelected() {
        viewModelScope.launch {
            val current = _uiState.value
            if (current is SitesScreenState.Content) {
                current.sites.filter { it.isSelected }.forEach {
                    siteInfoRepository.delete(it)
                }
            }
        }
    }

    fun openUrlInBrowser(url: String) {
        if (url.isBlank()) {
            _event.postValue(SitesScreenEvent.WrongUrl())
            return
        }
        val urlUpd = if (url.contains("http")) {
            url
        } else {
            "https://$url"
        }
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(urlUpd)
        ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }

        try {
            startActivity(appContext, intent, null)
        } catch (e: Exception) {
            _event.postValue(SitesScreenEvent.WrongUrl())
        }
    }

    fun resetEvent() {
        _event.postValue(SitesScreenEvent.Default)
    }
}
