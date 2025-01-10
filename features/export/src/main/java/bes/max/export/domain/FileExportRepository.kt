package bes.max.export.domain

import android.net.Uri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface FileExportRepository {

    suspend fun export(dispatcher: CoroutineDispatcher = Dispatchers.IO): String

    suspend fun import(uri: Uri, code: String, dispatcher: CoroutineDispatcher = Dispatchers.IO)

}