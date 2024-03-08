package bes.max.cipher.api

interface CipherApi {
    fun encrypt(alias: String, textToEncrypt: String): ByteArray

    fun decrypt(alias: String, encryptedData: ByteArray, encryptionIv: ByteArray): String
}