package bes.max.passman.cipher

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProtection
import android.util.Base64
import androidx.annotation.RequiresApi
import bes.max.cipher.api.CipherApi
import bes.max.cipher.api.EXPORT_ALIAS
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

    private fun getExportCode(key: SecretKey): String {
        val keyBytes = key.encoded ?: throw IllegalStateException("Key encoding is null")
        return Base64.encodeToString(keyBytes, Base64.DEFAULT)
    }

    private fun restoreExportKey(exportCode: String): SecretKey {
        val keyBytes = Base64.decode(exportCode, Base64.DEFAULT)
        val key = javax.crypto.spec.SecretKeySpec(keyBytes, ALGORITHM)
        return key
    }

    override fun encryptExportData(textToEncrypt: String): Pair<EncryptedData, String> {
        val key = generateSoftwareKey()
        val encryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, key)
        }
        val bytesToEncrypt = textToEncrypt.encodeToByteArray()
        val encryptedBytes = encryptCipher.doFinal(bytesToEncrypt)
        val initVector = encryptCipher.iv
        val initVectorAsString = Base64.encodeToString(initVector, Base64.DEFAULT)
        val encryptedStr = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        return EncryptedData(encryptedStr, initVectorAsString) to getExportCode(key)
    }

    override fun decryptExportData(encryptedData: String, exportCode: String, initVector: String): String {
        val key = restoreExportKey(exportCode)
        val decryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(
                Cipher.DECRYPT_MODE,
                key,
                IvParameterSpec(Base64.decode(initVector, Base64.DEFAULT))
            )
        }
        val decryptedBytes = decryptCipher.doFinal(Base64.decode(encryptedData, Base64.DEFAULT))
        return decryptedBytes.decodeToString()
    }

    private fun generateSoftwareKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(ALGORITHM)
        keyGenerator.init(256)
        return keyGenerator.generateKey()
    }
}