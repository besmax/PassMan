package bes.max.features.main.ui.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

fun Context.copyTextToClipboard(textToCopy: String) {
    val clipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("label", textToCopy.trim())
    clipboard.setPrimaryClip(clip)
}