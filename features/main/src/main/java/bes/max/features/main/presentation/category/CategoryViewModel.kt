package bes.max.features.main.presentation.category

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bes.max.features.main.domain.models.CategoryModelMain
import bes.max.features.main.domain.repositories.CategoriesRepository
import bes.max.features.main.ui.util.categoryColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoriesRepository,
) : ViewModel() {

    private val _uiState = MutableLiveData<CategoryScreenState>(CategoryScreenState.Loading)
    val uiState: LiveData<CategoryScreenState> = _uiState

    fun getCategories() {
        viewModelScope.launch {
            val categories = categoryRepository.getAll().firstOrNull() ?: return@launch
            val availableColors =
                categoryColors.filter { color -> (categories.find { it.color == color } == null) }
            _uiState.postValue(
                CategoryScreenState.Content(
                    categories = categories,
                    colors = availableColors
                )
            )
        }
    }

    fun addCategory(color: Color, name: String? = null) {
        viewModelScope.launch {
            categoryRepository.insert(
                CategoryModelMain(
                    name = name ?: "",
                    color = color,
                )
            )
        }
    }
}