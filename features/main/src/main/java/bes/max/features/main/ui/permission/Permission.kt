package bes.max.features.main.ui.permission

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState

val permissions26Api = listOf(
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_EXTERNAL_STORAGE,
)

@RequiresApi(Build.VERSION_CODES.R)
val permissions30Api = listOf(
    Manifest.permission.MANAGE_EXTERNAL_STORAGE
)


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
val permissions33Api: List<String> = listOf(
   // Manifest.permission.POST_NOTIFICATIONS,
)

fun getPermissions() = buildList<String> {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) addAll(permissions30Api)
    else addAll(permissions26Api)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) addAll(permissions33Api)
}

@OptIn(ExperimentalPermissionsApi::class)
fun MultiplePermissionsState.check() {
    if (this.allPermissionsGranted.not()) this.launchMultiplePermissionRequest()
}