package bes.max.features.main.ui

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bes.max.features.main.ui.icon.importIcon
import bes.max.passman.features.main.R
import bes.max.ui.common.ShowTitle
import bes.max.ui.common.UserInput

@SuppressLint("UnrememberedMutableState")
@Composable
fun SettingsScreen(
    navigateBack: () -> Unit,
    navigateToFileExplorer: () -> Unit,
    export: () -> Unit,
    import: (Uri, String) -> Unit,
    importCode: String?,
    resetImportCode: () -> Unit,
    eventMessage: String?,
    resetEvent: () -> Unit,
) {
    var inputCode by remember { mutableStateOf("") }
    var showEnterCode by remember { mutableStateOf(false) }
    var importFileUri by remember { mutableStateOf(Uri.parse("")) }
    val pickFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { fileUri ->
        if (fileUri != null) {
            importFileUri = fileUri
            showEnterCode = true
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        ShowTitle(
            title = stringResource(R.string.settings),
            goBack = navigateBack,
        )

        Spacer(Modifier.height(16.dp))

        SettingsItem(
            text = stringResource(R.string.settings_item_import),
            onItemClick = { pickFileLauncher.launch("text/csv") }, //for all types=*/*
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

    if (importCode != null) {
        ShowImportCode(
            code = importCode,
            onClose = resetImportCode,
        )
    }

    if (showEnterCode) {
        EnterImportCode(
            confirm = { input ->
                import(importFileUri, input)
            },
            cancel = { showEnterCode = false }
        )
    }

    if (eventMessage != null) {

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

@Composable
private fun ShowImportCode(
    code: String?,
    onClose: () -> Unit,
) {
    if (code == null) return

    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Text(
                text = stringResource(R.string.import_code_placeholder, code),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = stringResource(R.string.import_code_descr),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(onClick = onClose) {
                Text(
                    text = stringResource(R.string.close),
                    textAlign = TextAlign.Center
                )
            }
        },
    )
}

@Composable
private fun EnterImportCode(
    cancel: () -> Unit,
    confirm: (String) -> Unit,
) {
    var inputCode by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = cancel,
        title = {
            Text(
                text = stringResource(R.string.enter_import_code),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Box(
                Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                UserInput(
                    hintRes = R.string.enter_import_code,
                    onValueChanged = { inputCode = it },
                    maxLines = 3,
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                confirm(inputCode)
                cancel()
            }) {
                Text(
                    text = stringResource(R.string.do_import),
                    textAlign = TextAlign.Center
                )
            }
        },
        dismissButton = {
            Button(onClick = cancel) {
                Text(
                    text = stringResource(R.string.cancel),
                    textAlign = TextAlign.Center
                )
            }
        },
    )
}