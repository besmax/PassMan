package bes.max.main.presentation.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bes.max.cipher.api.CipherApi
import bes.max.main.domain.repositories.SiteInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val siteInfoRepository: SiteInfoRepository,
    private val cipher: CipherApi
) : ViewModel() {

    private val id = savedStateHandle.get<Int>("id")
    private val _uiState: MutableLiveData<EditScreenState> = MutableLiveData(EditScreenState.Loading)
    val uiState: LiveData<EditScreenState> = _uiState

    init {
        if (id != -1) {
            getSiteModel(id!!)
        } else {
            _uiState.value = EditScreenState.New
        }
    }

    private fun getSiteModel(id: Int) {
        viewModelScope.launch {
            val model = siteInfoRepository.getById(id)
            if (model!= null) {
                _uiState.postValue(EditScreenState.Edit(model))
            } else {
                _uiState.postValue(EditScreenState.Error)
            }
        }
    }


}