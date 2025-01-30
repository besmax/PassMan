package bes.max.export.presentation

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bes.max.export.domain.FileExportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ExportViewModel"

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val fileExportRepository: FileExportRepository,
) : ViewModel() {

    private val _event = MutableLiveData<ExportEvent?>()
    val event: LiveData<ExportEvent?> = _event

    private val _code = MutableLiveData<Pair<String, Uri>?>()
    val code: LiveData<Pair<String, Uri>?> = _code

    fun export() {
        viewModelScope.launch {
            val codeAndUri = fileExportRepository.export()
            _code.postValue(codeAndUri)
        }
    }

    fun import(fileUri: Uri, code: String) {
        viewModelScope.launch {
            try {
                fileExportRepository.import(fileUri, code)
            } catch (e: Exception) {
                _event.postValue(ExportEvent.WrongImportCodeEvent())
                Log.e(TAG, "Fail import with e: $e")
            }
        }
    }

    fun resetCode() {
        _code.postValue(null)
    }

    fun resetEvent() {
        _event.postValue(null)
    }
}