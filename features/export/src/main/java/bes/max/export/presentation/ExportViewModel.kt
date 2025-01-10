package bes.max.export.presentation

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bes.max.export.domain.FileExportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val fileExportRepository: FileExportRepository,
) : ViewModel() {

    private val _event = MutableLiveData<ExportEvent>()
    val event: LiveData<ExportEvent> = _event

    fun export() {
        viewModelScope.launch {
            val importCode = fileExportRepository.export()
            _event.postValue(ExportEvent.ShowExportCodeEvent(importCode))
        }
    }

    fun import(fileUri: Uri, code: String) {
        viewModelScope.launch {
            fileExportRepository.import(fileUri, code)
        }
    }
}