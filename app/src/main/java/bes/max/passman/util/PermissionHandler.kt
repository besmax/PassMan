package bes.max.passman.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class PermissionHandler @Inject constructor() {

    companion object {
        const val PERMISSION_REQUEST_CODE = 991

        val PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        val PERMISSIONS_HIGHER_API = arrayOf(
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
        )
    }

    fun checkPermissions(@ActivityContext context: Context) {
        val permissionsToRequest = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.addAll(PERMISSIONS_HIGHER_API)
        }

        for (permission in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(
                    context, permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissions(
                context as Activity,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
            // Requires launching activity that explicitly asks storage permission for the app
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager() && permissionsToRequest.contains(
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                )
            ) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.setData(Uri.parse(Uri.parse("package:" + context.packageName).toString()))
                context.startActivityForResult(intent, PERMISSION_REQUEST_CODE)
            }
        } else {
            return
        }
    }
}