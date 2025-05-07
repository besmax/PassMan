package bes.max.features.main.presentation.edit

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bes.max.cipher.api.CipherApi
import bes.max.features.main.domain.models.SiteInfoModelMain
import bes.max.features.main.domain.repositories.CategoriesRepository
import bes.max.features.main.domain.repositories.SiteInfoRepository
import bes.max.passman.features.main.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val siteInfoRepository: SiteInfoRepository,
    private val cipher: CipherApi,
    private val categoriesRepository: CategoriesRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val id = savedStateHandle.get<Int>("id")
    private val _uiState: MutableLiveData<EditScreenState> =
        MutableLiveData(EditScreenState.Loading)
    val uiState: LiveData<EditScreenState> = _uiState

    private val _name = MutableStateFlow<String>("")
    val name = _name.asStateFlow()

    private val _url = MutableStateFlow<String>("")
    val url = _url.asStateFlow()

    private val _password = MutableStateFlow<PasswordState>(PasswordState(""))
    val password = _password.asStateFlow()

    private val _login = MutableStateFlow<String>("")
    val login = _login.asStateFlow()

    private val _comment = MutableStateFlow<String>("")
    val comment = _comment.asStateFlow()

    private val _color = MutableStateFlow<Int?>(null)
    val color = _color.asStateFlow()

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
                updateAllFieldsByModel(model)
            } else {
                _uiState.postValue(EditScreenState.Error)
            }
        }
    }

    private fun updateAllFieldsByModel(model: SiteInfoModelMain) {
        _name.update { model.name }
        _url.update { model.url }
        _password.update { it.copy(password = model.password) }
        _login.update { model.login ?: "" }
        _comment.update { model.description ?: "" }
        _color.update { model.categoryColor }
    }

    fun showPassword(model: SiteInfoModelMain? = null) {
        val password = if (model != null) {
            cipher.decrypt(
                alias = model.name,
                encryptedData = model.password,
                initVector = model.passwordIv
            )
        } else {
            password.value.password
        }
        _password.update {
            it.copy(
                password = password,
                hiden = false
            )
        }
    }

    fun update(model: SiteInfoModelMain) {
        val urlUpd = url.value.ifBlank { model.url }
        val https = appContext.getString(R.string.init_url)
        val passwordState = password.value
        val (password, passwordIv) = if (model.password != passwordState.password) {
            val encryptedData =
                cipher.encrypt(name.value.ifBlank { model.name }, password.value.password)
            encryptedData.encryptedData to encryptedData.passwordIv
        } else if (model.name != (name.value.ifBlank { model.name })) {
            val decryptedPassword = cipher.decrypt(
                model.name,
                model.password,
                model.passwordIv
            )
            val encryptedData =
                cipher.encrypt(name.value.ifBlank { model.name }, decryptedPassword)
            encryptedData.encryptedData to encryptedData.passwordIv
        } else {
            model.password to model.passwordIv
        }
        val updatedModel =  model.copy(
            name = name.value.ifBlank { model.name },
            password = password,
            url = if (urlUpd.contains(https.take(4))) urlUpd else "$https$urlUpd",
            passwordIv = passwordIv,
            description = if (comment.value.isBlank()) model.description else comment.value.ifBlank { null },
            categoryColor = _color.value,
            login = if (login.value.isBlank()) model.login else login.value.ifBlank { null }
        )

        viewModelScope.launch {
            siteInfoRepository.update(updatedModel)
        }
    }

    fun add() {
        val https = appContext.getString(R.string.init_url)
        viewModelScope.launch {
            val encryptedData = cipher.encrypt(
                alias = name.value.trim(),
                textToEncrypt = password.value.password.trim()
            )
            siteInfoRepository.create(
                SiteInfoModelMain(
                    name = name.value.trim(),
                    password = encryptedData.encryptedData,
                    url = if (url.value.contains(https.take(4))) url.value else "$https${url.value}",
                    passwordIv = encryptedData.passwordIv,
                    description = if (comment.value.isBlank()) null else comment.value.trim(),
                    categoryColor = color.value,
                    login = login.value,
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

    fun onNameChanged(name: String) {
        if (name != _name.value) _name.update { name }
    }

    fun onUrlChanged(url: String) {
        if (_url.value != url) _url.update { url }
    }

    fun onPasswordChanged(password: String) {
        if (_password.value.password != password) _password.update {
            it.copy(
                password = password,
                changed = true
            )
        }
    }

    fun onCommentChanged(comment: String?) {
        if (_comment.value != comment) _comment.update { comment ?: "" }
    }

    fun onLoginChanged(login: String?) {
        if (_login.value != login) _login.update { login ?: "" }
    }

    fun onCategoryChanged(category: Int?) {
        if (_color.value != category) _color.update { category }
    }
}
