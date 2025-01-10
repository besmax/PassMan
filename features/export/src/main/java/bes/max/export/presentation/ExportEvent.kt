package bes.max.export.presentation

sealed interface ExportEvent {
    data class ShowExportCodeEvent(val code: String) : ExportEvent
}