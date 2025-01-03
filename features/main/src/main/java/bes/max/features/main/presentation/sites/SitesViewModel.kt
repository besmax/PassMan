package bes.max.features.main.presentation.sites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bes.max.cipher.api.CipherApi
import bes.max.features.main.domain.models.SiteInfoModelMain
import bes.max.features.main.domain.repositories.CategoriesRepository
import bes.max.features.main.domain.repositories.SiteInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SitesViewModel @Inject constructor(
    private val siteInfoRepository: SiteInfoRepository,
    private val cipher: CipherApi,
    private val categoriesRepository: CategoriesRepository,
) : ViewModel() {

    private val _uiState = MutableLiveData<SitesScreenState>(SitesScreenState.Loading)
    val uiState: LiveData<SitesScreenState> = _uiState

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
                _uiState.postValue(currentState.copy(filters = filters))
            }
        }
    }

    private fun filterSites(color: Int? = null) {
        val currentState = uiState.value
        if (currentState is SitesScreenState.Content) {
            if (color == null || color == -1) {
                _uiState.postValue(currentState.copy(filteredSites = currentState.sites))
            } else {
                _uiState.postValue(currentState.copy(
                    filteredSites = currentState.sites.filter { it.categoryColor == color }
                ))
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

}