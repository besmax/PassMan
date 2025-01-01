package bes.max.export.ui

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun FileExplorerScreen(
    navigateBack: () -> Unit,
) {
    RequestPermission(
        permissions = setOf(READ_EXTERNAL_STORAGE),
        onNotGranted = navigateBack
    )

    var directory by remember { mutableStateOf(Environment.getExternalStorageDirectory().path) }

    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri != null) {
            // Handle the selected folder URI
        }
    }

}
