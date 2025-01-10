package bes.max.export.data

import android.content.Context
import android.net.Uri
import bes.max.cipher.api.CipherApi
import bes.max.cipher.api.EXPORT_ALIAS
import bes.max.database.api.model.CategoryModel
import bes.max.database.api.model.SiteInfoModel
import bes.max.export.domain.FileReader
import kotlinx.serialization.json.Json

private const val TAG = "FileReaderImpl"

class FileReaderImpl(private val context: Context, private val cipher: CipherApi) : FileReader {

    override fun readData(fileUri: Uri, exportCode: String): Map<String, List<Any>> {
        return context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
            val (passwordIv, fileContentEncrypted) = inputStream.bufferedReader().use { reader ->
                val lines = reader.readLines()
                val firstLine = lines.firstOrNull() ?: ""
                val remainingContent = lines.drop(1).joinToString("\n")
                firstLine to remainingContent
            }
            val fileContent = cipher.decryptExportData(fileContentEncrypted, exportCode, passwordIv)
            convertToMap(fileContent)
        } ?: emptyMap()
    }

    private fun convertToMap(data: String): Map<String, List<Any>> = buildMap {
        data.lineSequence().iterator().let { iterator ->
            while (iterator.hasNext()) {
                val headerLine = iterator.next()
                val (header, dataSize) = headerLine.split(HEADER_SEPARATOR)
                if (iterator.hasNext()) {
                    val dataLine = iterator.next()
                    val list = readList(header, dataLine)
                    put(header, list)
                }
            }
        }
    }

    private fun readList(header: String, list: String): List<Any> {
        return when (header) {
            SiteInfoModel.Companion::class.java.name -> {
                Json.decodeFromString<List<SiteInfoModel>>(list)
            }

            CategoryModel.Companion::class.java.name -> {
                Json.decodeFromString<List<CategoryModel>>(list)
            }

            else -> {
                error("Unknown model")
            }
        }
    }
}