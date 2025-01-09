package bes.max.export.data

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import bes.max.database.api.model.CategoryModel
import bes.max.database.api.model.SiteInfoModel
import bes.max.export.domain.FileWriter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val DEFAULT_NAME = "passman_backup.csv"
const val HEADER_SEPARATOR = "|||"

class FileWriterImpl(private val context: Context) : FileWriter {

    override fun writeData(headerDataMap: Map<String, List<Any>>) {
        val contentResolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, DEFAULT_NAME)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
            ?.let { uri ->
                val file = contentResolver.openOutputStream(uri)
                file?.bufferedWriter()?.use { writer ->
                    headerDataMap.entries.forEach { headerDataEntry ->
                        val headerLine = headerDataEntry.buildHeader()
                        writer.write(headerLine)
                        writer.newLine()
                        val data: String =
                            when (headerDataEntry.key) {
                                SiteInfoModel.Companion::class.java.name -> {
                                    Json.encodeToString(headerDataEntry.value as List<SiteInfoModel>)
                                }

                                CategoryModel.Companion::class.java.name -> {
                                    Json.encodeToString(headerDataEntry.value as List<CategoryModel>)
                                }

                                else -> {
                                    error("Unknown model")
                                }
                            }

                        writer.write(data)
                    }
                }
            }
    }

    private fun <T> Map.Entry<String, List<T>>.buildHeader() = buildString {
        append(this@buildHeader.key)
        append(HEADER_SEPARATOR)
        append(this@buildHeader.value.size)
    }

}