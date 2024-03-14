package bes.max.cipher.api

import bes.max.cipher.model.EncryptedData

interface CipherApi {
    fun encrypt(alias: String, textToEncrypt: String): EncryptedData

    fun decrypt(alias: String, encryptedData: String, initVector: String): String
}