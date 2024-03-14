package bes.max.features.main.presentation.sites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bes.max.cipher.api.CipherApi
import bes.max.features.main.domain.models.SiteInfoModelMain
import bes.max.features.main.domain.repositories.SiteInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SitesViewModel @Inject constructor(
    private val siteInfoRepository: SiteInfoRepository,
    private val cipher: CipherApi
) : ViewModel() {

    private val _uiState = MutableLiveData<SitesScreenState>(SitesScreenState.Loading)
    val uiState: LiveData<SitesScreenState> = _uiState

    init {
        getSites()
    }


    fun getSites() {
        _uiState.value = SitesScreenState.Loading
        viewModelScope.launch {
            siteInfoRepository.getAll().collect() { list ->
                if (list.isNotEmpty()) {
                    _uiState.postValue(SitesScreenState.Content(list))
                } else {
                    _uiState.postValue(SitesScreenState.Empty)
                }
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