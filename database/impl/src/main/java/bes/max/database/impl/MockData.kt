package bes.max.database.impl

import android.os.Build
import androidx.annotation.RequiresApi
import bes.max.database.impl.entities.SiteInfoEntity
import bes.max.passman.cipher.CipherImpl

@RequiresApi(Build.VERSION_CODES.M)
internal object MockData {

    private val cipher = CipherImpl()


    val list = listOf(
        SiteInfoEntity(
            password = cipher.encrypt("vk", "qwerty123").toString(),
            name = "vk",
            url = "https://vk.com"
        ),
        SiteInfoEntity(
            password = cipher.encrypt("youtube", "123qwerty").toString(),
            name = "youtube",
            url = "https://youtube.com"
        ),
        SiteInfoEntity(
            password = cipher.encrypt("wikipedia", "qwe123rty").toString(),
            name = "wikipedia",
            url = "https://www.wikipedia.org/"
        ),
        SiteInfoEntity(
            password = cipher.encrypt("stackoverflow", "12qw3e").toString(),
            name = "stackoverflow",
            url = "https://stackoverflow.com/"
        ),
    )

}