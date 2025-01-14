package bes.max.export.domain

interface FileWriter {

    /** Write data to csv file in Environment.DIRECTORY_DOWNLOADS and return code for importing data */
    fun writeData(headerDataMap: Map<String, List<Any>>): String

}