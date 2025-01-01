package bes.max.export.ui

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun RequestPermission(
    permissions: Set<String>,
    onGranted: (() -> Unit)? = null,
    onNotGranted: (() -> Unit)? = null,
) {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onGranted?.invoke()
        } else {
            onNotGranted?.invoke()
        }
    }

    permissions.forEach { permission ->
        val notGranted = ContextCompat.checkSelfPermission(
            LocalContext.current,
            permission
        ) != PackageManager.PERMISSION_GRANTED

        if (notGranted) {
            permissionLauncher.launch(permission)
        }
    }
}