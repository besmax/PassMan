package bes.max.export.domain

interface FileWriter {

    fun writeData(headerDataMap: Map<String, List<Any>>)

}