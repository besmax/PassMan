package bes.max.export.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bes.max.export.domain.FileExportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val fileExportRepository: FileExportRepository,
) : ViewModel(){

    fun export() {
        viewModelScope.launch {
            fileExportRepository.export()
        }
    }

    fun import(fileUri: Uri) {
        viewModelScope.launch {
            fileExportRepository.import(fileUri)
        }
    }
}