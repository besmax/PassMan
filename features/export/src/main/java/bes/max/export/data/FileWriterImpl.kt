package bes.max.export.data

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import bes.max.cipher.api.CipherApi
import bes.max.cipher.api.EXPORT_ALIAS
import bes.max.database.api.model.CategoryModel
import bes.max.database.api.model.SiteInfoModel
import bes.max.export.domain.FileWriter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val DEFAULT_NAME = "passman_backup.csv"
const val HEADER_SEPARATOR = "|||"

class FileWriterImpl(
    private val context: Context,
    private val cipher: CipherApi,
) : FileWriter {

    override fun writeData(headerDataMap: Map<String, List<Any>>): Pair<String, Uri> {
        val contentResolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, DEFAULT_NAME)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val exportedCode =
            contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
                ?.let { uri ->
                    val file = contentResolver.openOutputStream(uri)
                    val code = file?.bufferedWriter()?.use { writer ->
                        val data = convertDataToString(headerDataMap)
                        val (encryptedData, exportedCode) = cipher.encryptExportData(data)
                        writer.write(encryptedData.passwordIv)
                        writer.newLine()
                        writer.write(encryptedData.encryptedData)
                        exportedCode
                    } ?: error("Can not write data")
                    code to uri
                }
        return exportedCode ?: error("Can not write data")
    }

    private fun convertDataToString(headerDataMap: Map<String, List<Any>>) = buildString {
        headerDataMap.entries.forEachIndexed { index, headerDataEntry ->
            val headerLine = headerDataEntry.buildHeader()
            append(headerLine)
            append(System.lineSeparator())
            val data = parseByHeader(headerDataEntry.key, headerDataEntry.value)
            append(data)
            if (index != headerDataMap.size - 1) append(System.lineSeparator())
        }
    }

    private fun parseByHeader(header: String, data: List<Any>): String {
        return when (header) {
            SiteInfoModel.Companion::class.java.name -> {
                Json.encodeToString(data as List<SiteInfoModel>)
            }

            CategoryModel.Companion::class.java.name -> {
                Json.encodeToString(data as List<CategoryModel>)
            }

            else -> {
                error("Unknown model")
            }
        }
    }

    private fun <T> Map.Entry<String, List<T>>.buildHeader() = buildString {
        append(this@buildHeader.key)
        append(HEADER_SEPARATOR)
        append(this@buildHeader.value.size)
    }

}