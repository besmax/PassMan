package bes.max.export.domain

import android.net.Uri

interface FileReader {
    fun readData(fileUri: Uri, exportCode: String): Map<String, List<Any>>
}