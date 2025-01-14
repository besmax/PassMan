package bes.max.export.util

import java.io.File

fun File.getFormattedFileSize() : String {
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    var size = length().toDouble()
    var unitIndex = 0

    while (size > 1024 && unitIndex < units.size - 1) {
        size /= 1024
        unitIndex++
    }

    return "%.2f %s".format(size, units[unitIndex])
}
