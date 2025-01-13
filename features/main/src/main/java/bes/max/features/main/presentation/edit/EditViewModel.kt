package bes.max.features.main.presentation.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
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
class EditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val siteInfoRepository: SiteInfoRepository,
    private val cipher: CipherApi,
    private val categoriesRepository: CategoriesRepository,
) : ViewModel() {

    private val id = savedStateHandle.get<Int>("id")
    private val _uiState: MutableLiveData<EditScreenState> =
        MutableLiveData(EditScreenState.Loading)
    val uiState: LiveData<EditScreenState> = _uiState

    init {
        if (id != -1) {
            getSiteModel(id!!)
        } else {
            _uiState.value = EditScreenState.New()
            getCategories()
        }
    }

    private fun getSiteModel(id: Int) {
        viewModelScope.launch {
            val model = siteInfoRepository.getById(id)
            if (model != null) {
                _uiState.postValue(EditScreenState.Edit(model))
                getCategories()
            } else {
                _uiState.postValue(EditScreenState.Error)
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

    fun update(
        model: SiteInfoModelMain,
        name: String,
        url: String,
        password: String,
        comment: String?,
        categoryColor: Int?,
    ) {
        //model with  password which is not encrypted
        val partiallyUpdatedModel = if (password.isNotBlank()) {
            model.copy(
                name = name.ifBlank { model.name },
                password = password,
                url = url.ifBlank { model.url },
                passwordIv = "",
                description = if (comment.isNullOrBlank()) model.description else comment,
                categoryColor = categoryColor,
            )
        } else {
            model.copy(
                name = name.ifBlank { model.name },
                password = cipher.decrypt(model.name, model.password, model.passwordIv),
                url = url.ifBlank { model.url },
                description = if (comment.isNullOrBlank()) model.description else comment,
                categoryColor = categoryColor,
            )
        }
        val encryptedData =
            cipher.encrypt(partiallyUpdatedModel.name, partiallyUpdatedModel.password)
        val updatedModel = partiallyUpdatedModel.copy(
            password = encryptedData.encryptedData,
            passwordIv = encryptedData.passwordIv,
        )

        viewModelScope.launch {
            siteInfoRepository.update(updatedModel)
        }
    }

    fun add(name: String, url: String, password: String, comment: String?, categoryColor: Int?) {
        val encryptedData = cipher.encrypt(alias = name.trim(), textToEncrypt = password.trim())
        viewModelScope.launch {
            siteInfoRepository.create(
                SiteInfoModelMain(
                    name = name.trim(),
                    password = encryptedData.encryptedData,
                    url = url.trim(),
                    passwordIv = encryptedData.passwordIv,
                    description = if (comment?.isBlank() == true) null else comment?.trim(),
                    categoryColor = categoryColor,
                )
            )
        }
    }

    fun delete(model: SiteInfoModelMain) {
        viewModelScope.launch {
            siteInfoRepository.delete(model)
        }
    }

    private fun getCategories() {
        viewModelScope.launch {
            categoriesRepository.getAll().collect() { categories ->
                val currentState = uiState.value
                if (currentState is EditScreenState.New) {
                    _uiState.postValue(currentState.copy(categories = categories))
                } else if (currentState is EditScreenState.Edit) {
                    _uiState.postValue(currentState.copy(categories = categories))
                }
            }
        }
    }

}