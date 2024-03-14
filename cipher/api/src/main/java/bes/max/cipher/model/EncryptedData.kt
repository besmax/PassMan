package bes.max.cipher.model

data class EncryptedData(
    val encryptedData: String,
    val passwordIv: String,
)
