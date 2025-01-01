package bes.max.passman.cipher

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProtection
import android.util.Base64
import androidx.annotation.RequiresApi
import bes.max.cipher.api.CipherApi
import bes.max.cipher.model.EncryptedData
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
const val EXPORT_ALIAS = "export alias"

@RequiresApi(Build.VERSION_CODES.R)
object CipherImpl : CipherApi {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }


    private fun getKey(alias: String): SecretKey {
        val existingKey = keyStore.getEntry(alias, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey(alias)
    }

    private fun createKey(alias: String): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    alias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    override fun encrypt(alias: String, textToEncrypt: String): EncryptedData {
        val encryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getKey(alias))
        }
        val bytesToEncrypt = textToEncrypt.encodeToByteArray()
        val encryptedBytes = encryptCipher.doFinal(bytesToEncrypt)
        val initVector = encryptCipher.iv
        val initVectorAsString = Base64.encodeToString(initVector, Base64.DEFAULT)
        val encryptedStr = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        return EncryptedData(encryptedStr, initVectorAsString)
    }

    override fun decrypt(alias: String, encryptedData: String, initVector: String): String {
        val decryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(
                Cipher.DECRYPT_MODE,
                getKey(alias),
                IvParameterSpec(Base64.decode(initVector, Base64.DEFAULT))
            )
        }
        val decryptedBytes = decryptCipher.doFinal(Base64.decode(encryptedData, Base64.DEFAULT))
        return decryptedBytes.decodeToString()
    }

    override fun getExportCode(): String {
        val key = getKey(EXPORT_ALIAS)
        val keyGenerator = KeyGenerator.getInstance(ALGORITHM)
        keyGenerator.init(key.encoded.size * 8)
        val wrappingKey = keyGenerator.generateKey()
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.WRAP_MODE, wrappingKey)
        val wrappedKeyBytes = cipher.wrap(key)
        val wrappingKeyBytes = wrappingKey.encoded
        val combinedBytes = wrappingKeyBytes + wrappedKeyBytes
        return Base64.encodeToString(combinedBytes, Base64.DEFAULT)
    }

    override fun restoreExportKey(exportCode: String) {
        val combinedBytes = Base64.decode(exportCode, Base64.DEFAULT)
        val wrappingKeySize = 32
        val wrappingKeyBytes = combinedBytes.copyOfRange(0, wrappingKeySize)
        val wrappedKeyBytes = combinedBytes.copyOfRange(wrappingKeySize, combinedBytes.size)
        val wrappingKey = javax.crypto.spec.SecretKeySpec(wrappingKeyBytes, ALGORITHM)
        val key = unwrapSecretKey(wrappingKey, wrappedKeyBytes)
        importKeyIntoKeyStore(key)
    }

    private fun unwrapSecretKey(wrappingKey: SecretKey, wrappedKey: ByteArray): SecretKey {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.UNWRAP_MODE, wrappingKey)
        return cipher.unwrap(wrappedKey, ALGORITHM, Cipher.SECRET_KEY) as SecretKey
    }

    private fun importKeyIntoKeyStore(secretKey: SecretKey, alias: String = EXPORT_ALIAS,) {
        val keyEntry = KeyStore.SecretKeyEntry(secretKey)
        val keyProtection = KeyProtection.Builder(
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(false)
            .build()

        keyStore.setEntry(alias, keyEntry, keyProtection)
    }
}