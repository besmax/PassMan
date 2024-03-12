package bes.max.passman.di

import android.os.Build
import androidx.annotation.RequiresApi
import bes.max.database.impl.entities.SiteInfoEntity
import bes.max.passman.cipher.CipherImpl

@RequiresApi(Build.VERSION_CODES.M)
internal object MockData {

    private val cipher = CipherImpl
    private val vkEncryptedData = cipher.encrypt("vk", "qwerty123")
    private val youtubeEncryptedData = cipher.encrypt("youtube", "123qwerty")
    private val wikipediaEncryptedData = cipher.encrypt("wikipedia", "qwe123rty")
    private val stackoverflowEncryptedData = cipher.encrypt("stackoverflow", "12qw3e")


    val list = listOf(
        SiteInfoEntity(
            password = vkEncryptedData.encryptedData,
            name = "vk",
            url = "https://vk.com",
            passwordIv = vkEncryptedData.passwordIv
        ),
        SiteInfoEntity(
            password = youtubeEncryptedData.encryptedData,
            name = "youtube",
            url = "https://youtube.com",
            passwordIv = youtubeEncryptedData.passwordIv
        ),
        SiteInfoEntity(
            password = wikipediaEncryptedData.encryptedData,
            name = "wikipedia",
            url = "https://www.wikipedia.org/",
            passwordIv = wikipediaEncryptedData.passwordIv
        ),
        SiteInfoEntity(
            password = stackoverflowEncryptedData.encryptedData,
            name = "stackoverflow",
            url = "https://stackoverflow.com/",
            passwordIv = stackoverflowEncryptedData.passwordIv
        ),
    )

}