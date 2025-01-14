package bes.max.export.presentation

import bes.max.export.R

sealed interface ExportEvent {
    data class ShowExportCodeEvent(val code: String) : ExportEvent
    data class WrongImportCodeEvent(val messageResId: Int = R.string.wrong_import_code) : ExportEvent

}