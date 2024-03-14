package bes.max.passman.cipher

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
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

}