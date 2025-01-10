package bes.max.cipher.api

import bes.max.cipher.model.EncryptedData

const val EXPORT_ALIAS = "export alias"

interface CipherApi {
    fun encrypt(alias: String, textToEncrypt: String): EncryptedData

    fun decrypt(alias: String, encryptedData: String, initVector: String): String

    fun getExportCode(): String

    fun restoreExportKey(exportCode: String)
}