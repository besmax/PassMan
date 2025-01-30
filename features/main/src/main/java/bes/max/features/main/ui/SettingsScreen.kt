package bes.max.features.main.ui

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import bes.max.features.main.presentation.settings.SettingsEvent
import bes.max.features.main.presentation.settings.SettingsViewModel
import bes.max.features.main.ui.icon.copyIcon
import bes.max.features.main.ui.icon.darkModeIcon
import bes.max.features.main.ui.icon.exportIcon
import bes.max.features.main.ui.icon.importIcon
import bes.max.features.main.ui.icon.lockIcon
import bes.max.features.main.ui.icon.lockOpenIcon
import bes.max.features.main.ui.util.copyTextToClipboard
import bes.max.passman.features.main.R
import bes.max.ui.common.Information
import bes.max.ui.common.ShowTitle
import bes.max.ui.common.UserInput
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@Composable
fun SettingsScreen(
    navigateBack: () -> Unit,
    export: () -> Unit,
    import: (Uri, String) -> Unit,
    importCodeAndUri: Pair<String, Uri>?,
    resetImportCode: () -> Unit,
    eventMessage: String?,
    resetEvent: () -> Unit,
    launchBiometric: (() -> Unit, () -> Unit) -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    val isNightModeActive by settingsViewModel.isNighModeActive.collectAsState()
    val pinCode by settingsViewModel.pinCode.collectAsState()
    val event by settingsViewModel.event.observeAsState()

    val scope = rememberCoroutineScope()

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

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        ShowTitle(
            title = stringResource(R.string.settings),
            navigateBack = navigateBack,
        )

        Spacer(Modifier.height(16.dp))

        SettingsItem(
            text = stringResource(R.string.settings_item_import),
            onItemClick = { pickFileLauncher.launch("*/*") },
            icon = importIcon,
            contentDescription = stringResource(R.string.settings_item_import_descr),
        )

        Spacer(Modifier.height(8.dp))

        SettingsItem(
            text = stringResource(R.string.settings_item_export),
            onItemClick = export,
            icon = exportIcon,
            contentDescription = stringResource(R.string.settings_item_export_descr),
        )

        Spacer(Modifier.height(8.dp))

        SwitchSettingsItem(
            text = stringResource(R.string.dark_mode),
            onSwitchClick = settingsViewModel::toggleDarkMode,
            checked = isNightModeActive,
            icon = darkModeIcon,
        )

        Spacer(Modifier.height(8.dp))

        SwitchSettingsItem(
            text = stringResource(R.string.use_pin_code),
            onSwitchClick = { use ->
                scope.launch {
                    if (settingsViewModel.haveRecords()) {
                        launchBiometric(
                            { settingsViewModel.togglePinCodeUsing(use) },
                            {}
                        )
                    } else {
                        settingsViewModel.togglePinCodeUsing(use)
                    }
                }
            },
            checked = pinCode?.active == true,
            icon = if (pinCode?.active == true) lockIcon else lockOpenIcon,
        )

        Spacer(Modifier.weight(1f))

        ShowMessageEvent(
            eventMessage = eventMessage,
            onDismiss = resetEvent,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.weight(1f))

        event?.let { PinCodeEvent(it, Modifier.align(Alignment.CenterHorizontally)) }
    }

    if (importCodeAndUri != null) {
        ShowImportCode(
            code = importCodeAndUri,
            onClose = resetImportCode,
            shareFile = settingsViewModel::shareBackupFile
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
}

@Composable
private fun PinCodeEvent(
    event: SettingsEvent,
    modifier: Modifier = Modifier
) {
    Crossfade(event, label = "ControlSettingsScreenEvent") { eventState ->
        when (eventState) {
            is SettingsEvent.ReCheckPinCode -> PinCodeInput(
                cancel = eventState.resetEvent,
                confirm = eventState.onSuccess,
                lastInput = eventState.pinCode
            )

            is SettingsEvent.TurnOnPinCode -> PinCodeInput(
                cancel = eventState.resetEvent,
                check = eventState.checkPinCode
            )

            SettingsEvent.Default -> {}

            is SettingsEvent.NoAppForSharing -> Information(
                title = stringResource(eventState.messageResId),
                modifier = modifier,
                onDismiss = eventState.resetEvent
            )
        }
    }
}

@Composable
private fun PinCodeInput(
    cancel: () -> Unit,
    confirm: (() -> Unit)? = null,
    check: ((String) -> Unit)? = null,
    lastInput: String? = null,
) {
    var pinCode by remember { mutableStateOf("") }
    var wrongPinCode by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = cancel,
        title = {
            Text(
                text = if (lastInput == null) stringResource(R.string.enter_pin_code)
                else stringResource(R.string.repeat_pin_code),
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
                    hintRes = if (lastInput != null) R.string.repeat_pin_code else R.string.enter_pin_code,
                    onValueChanged = { pinCode = it },
                    maxLines = 1,
                )
                if (wrongPinCode) {
                    Information(
                        title = stringResource(R.string.wrong_pin_code),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (lastInput != null) {
                    if (lastInput == pinCode) {
                        confirm?.invoke()
                    } else {
                        wrongPinCode = true
                    }
                } else {
                    check?.invoke(pinCode.trim())
                }
            }) {
                Text(
                    text = stringResource(R.string.save),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        },
        dismissButton = {
            TextButton(onClick = cancel) {
                Text(
                    text = stringResource(R.string.close),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    )
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
private fun SwitchSettingsItem(
    text: String,
    onSwitchClick: (Boolean) -> Unit,
    checked: Boolean,
    icon: ImageVector,
    contentDescription: String? = null,
    additionalContent: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
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

            Switch(
                checked = checked,
                onCheckedChange = { onSwitchClick(it) }
            )
        }

    }
}

@Composable
private fun ShowImportCode(
    code: Pair<String, Uri>?,
    onClose: () -> Unit,
    shareFile: (Uri) -> Unit
) {
    if (code == null) return
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.your_import_code),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = code.first.trim(),
                    modifier = Modifier
                        .clickable { context.copyTextToClipboard(code.first) },
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    ),
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.import_code_descr),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                TextButton(onClick = { shareFile(code.second) }) {
                    Text(
                        text = stringResource(R.string.share_file),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onClose) {
                Row {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close)
                    )
                    Text(
                        text = stringResource(R.string.close),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = { context.copyTextToClipboard(code.first) }) {
                Row {
                    Icon(
                        imageVector = copyIcon,
                        contentDescription = stringResource(R.string.copy_import_code)
                    )

                    Text(
                        text = stringResource(R.string.copy),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
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
            UserInput(
                hintRes = R.string.import_code,
                onValueChanged = { inputCode = it },
                maxLines = 3,
            )
        },
        confirmButton = {
            TextButton(onClick = {
                confirm(inputCode)
                cancel()
            }) {
                Row {
                    Icon(
                        imageVector = importIcon,
                        contentDescription = stringResource(R.string.do_import)
                    )

                    Text(
                        text = stringResource(R.string.do_import),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = cancel) {
                Row {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.cancel)
                    )

                    Text(
                        text = stringResource(R.string.cancel),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
    )
}

@Composable
private fun ShowMessageEvent(
    eventMessage: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Crossfade(eventMessage, label = "ShowMessageEvent") { message ->
        if (message != null) {
            Information(
                title = stringResource(R.string.wrong_import_code),
                text = stringResource(R.string.try_again),
                modifier = modifier,
                onDismiss = onDismiss
            )
        }
    }
}