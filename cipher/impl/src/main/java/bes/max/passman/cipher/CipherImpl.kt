package bes.max.passman.cipher

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import bes.max.cipher.api.CipherApi
import bes.max.cipher.model.EncryptedData
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"

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
                    .setRandomizedEncryptionRequired(false)
                    .build()
            )
        }.generateKey()
    }

    override fun encrypt(alias: String, textToEncrypt: String): EncryptedData {
        val encryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getKey(alias))
        }
        val bytesToEncrypt = textToEncrypt.encodeToByteArray()//Base64.decode(textToEncrypt, Base64.DEFAULT)              textToEncrypt.encodeToByteArray()
        val encryptedData = encryptCipher.doFinal(bytesToEncrypt)
        val initVector = encryptCipher.iv
        val ivString = Base64.encodeToString(initVector, Base64.DEFAULT)  //Base64.encodeToString(initVector, Base64.DEFAULT)          initVector.decodeToString()
        val encryptedStr = Base64.encodeToString(encryptedData, Base64.DEFAULT)//Base64.encodeToString(encryptedData, Base64.DEFAULT)   encryptedData.decodeToString()
        return EncryptedData(encryptedStr, ivString) //initVector.toString(Charsets.UTF_8)
    }

    override fun decrypt(alias: String, encryptedData: String, initVector: String): String {
        val decryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(
                Cipher.DECRYPT_MODE,
                getKey(alias),
                IvParameterSpec(Base64.decode(initVector, Base64.DEFAULT)) //initVector.toByteArray(Charsets.UTF_8)   initVector.encodeToByteArray()
            )
        }
        Log.e("AAAAAAAAAA", "${encryptedData}")
        val byteArr = decryptCipher.doFinal(Base64.decode(encryptedData, Base64.DEFAULT)) //decryptCipher.doFinal(encryptedData.encodeToByteArray())
        return byteArr.decodeToString()
    }


}

//.hexStringToByteArray()

//    fun String.hexStringToByteArray(): ByteArray {
//
//        val result = ByteArray(length / 2)
//
//        for (i in 0 until length step 2) {
//            val firstIndex = HEX_CHARS_STR.indexOf(this[i]);
//            val secondIndex = HEX_CHARS_STR.indexOf(this[i + 1]);
//
//            val octet = firstIndex.shl(4).or(secondIndex)
//            result.set(i.shr(1), octet.toByte())
//        }
//
//        return result
//    }
//
//
//    fun ByteArray.toHex(): String {
//        val result = StringBuffer()
//
//        forEach {
//            val st = String.format("%02x", it)
//            result.append(st)
//        }
//
//        return result.toString()
//    }