package bes.max.cipher.api

import bes.max.cipher.model.EncryptedData
import javax.crypto.SecretKey

const val EXPORT_ALIAS = "export alias"

interface CipherApi {
    fun encrypt(alias: String, textToEncrypt: String): EncryptedData

    fun decrypt(alias: String, encryptedData: String, initVector: String): String

    /** Encrypt data and return it and software generated key*/
    fun encryptExportData(textToEncrypt: String): Pair<EncryptedData, String>

    fun decryptExportData(encryptedData: String, exportCode: String, initVector: String): String
}