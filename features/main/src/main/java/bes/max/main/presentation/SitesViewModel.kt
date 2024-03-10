package bes.max.main.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bes.max.cipher.api.CipherApi
import bes.max.main.domain.repositories.SiteInfoRepository
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
                if (!list.isNullOrEmpty()) {
                    _uiState.postValue(SitesScreenState.Content(list))
                } else {
                    Log.e("AAAAAAAAAA", "${list.size}")
                    _uiState.postValue(SitesScreenState.Error)
                }
            }
        }
    }

    fun showPassword(alias: String, encryptedPassword: String, passwordIv: String): String {
        return cipher.decrypt(
            alias = alias,
            encryptedData = encryptedPassword,
            initVector = passwordIv
        )
    }

}