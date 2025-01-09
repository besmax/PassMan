package bes.max.features.main.ui

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import bes.max.features.main.presentation.edit.EditViewModel
import bes.max.features.main.ui.icon.importIcon
import bes.max.passman.features.main.R
import bes.max.ui.common.ShowTitle

@Composable
fun SettingsScreen(
    navigateBack: () -> Unit,
    navigateToFileExplorer: () -> Unit,
    export: () -> Unit,
    import: (Uri) -> Unit,
) {
//    val context = LocalContext.current
//    val multiplePermissionsState = rememberMultiplePermissionsState(getPermissions())
//    val requestPermissionsLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    ) { permissions ->
//        if (permissions.all { it.value }) {
//            Log.e("TAAAAAAAG","permissions=${permissions.entries.joinToString("||") { "perm=${it.key} res=${it.value}" }}")
//            Log.e("TAAAAAAAG", "permissionsS=${permissions.size}")
//            navigateToFileExplorer()
//        } else {
//            Log.e("TAAAAAAAG", "DENIED")
//            val permanentlyDenied = permissions.any { !it.value && !multiplePermissionsState.shouldShowRationale }
//            if (permanentlyDenied) {
//
//                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                intent.data = Uri.fromParts("package", context.packageName, null)
//                context.startActivity(intent)
//            }
//        }
//    }

    val pickFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { fileUri ->
        if (fileUri != null) {
            import(fileUri)
            Log.e("TAAAAAAAG", "fileUri=$fileUri")
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        ShowTitle(
            title = stringResource(R.string.settings),
            goBack = navigateBack,
        )

        Spacer(Modifier.height(16.dp))

        SettingsItem(
            text = stringResource(R.string.settings_item_import),
            onItemClick = { pickFileLauncher.launch("text/csv") }, //for all types=*/*
            //{ requestPermissionsLauncher.launch(getPermissions().toTypedArray()) },
            icon = importIcon,
            contentDescription = stringResource(R.string.settings_item_import_descr),
        )

        Spacer(Modifier.height(8.dp))

        SettingsItem(
            text = stringResource(R.string.settings_item_export),
            onItemClick = export,
            icon = importIcon,
            contentDescription = stringResource(R.string.settings_item_export_descr),
        )
    }
}

@Composable
private fun SettingsItem(
    text: String,
    onItemClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String? = null,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            .clickable { onItemClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = text
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = contentDescription,
                modifier = Modifier,
            )
        }

    }
}