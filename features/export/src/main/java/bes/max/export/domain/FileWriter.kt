package bes.max.export.domain

import android.net.Uri

interface FileWriter {

    /** Write data to csv file in Environment.DIRECTORY_DOWNLOADS and return code for importing data amd uri of file */
    fun writeData(headerDataMap: Map<String, List<Any>>): Pair<String, Uri>

}