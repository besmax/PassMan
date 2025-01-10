package bes.max.export.data

import android.content.Context
import android.net.Uri
import android.util.Log
import bes.max.database.api.model.CategoryModel
import bes.max.database.api.model.SiteInfoModel
import bes.max.export.domain.FileReader
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

private const val TAG = "FileReaderImpl"

class FileReaderImpl(private val context: Context) : FileReader {

    override fun readData(fileUri: Uri): Map<String, List<Any>> {
        return buildMap<String, List<Any>> {
            try {
                val inputStream = context.contentResolver.openInputStream(fileUri)

                if (inputStream != null) {
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    var line: String? = reader.readLine()

                    while (line != null) {
                        val (header, dataSize) = line.split(HEADER_SEPARATOR)
                        val list = readList(header, reader.readLine())
                        put(header, list)
                        line = reader.readLine()
                    }
                    inputStream.close()
                }
            } catch (e: IOException) {
                Log.e(TAG, "Can not readData from file with: $e")
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