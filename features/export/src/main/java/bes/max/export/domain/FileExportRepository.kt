package bes.max.export.domain

import android.net.Uri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface FileExportRepository {

    suspend fun export(dispatcher: CoroutineDispatcher = Dispatchers.IO)

    suspend fun import(uri: Uri, dispatcher: CoroutineDispatcher = Dispatchers.IO)

}