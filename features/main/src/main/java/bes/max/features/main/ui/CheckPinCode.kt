package bes.max.features.main.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import bes.max.passman.features.main.R
import bes.max.ui.common.Information
import bes.max.ui.common.UserInput

@Composable
fun CheckPinCode(
    onSuccess: () -> Unit,
    onFail: () -> Unit,
    checkPinInput: (String) -> Boolean,
) {
    var pinCode by remember { mutableStateOf("") }
    var wrongPinCode by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(
                text = stringResource(R.string.enter_pin_code),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Box(
                Modifier
            ) {
                UserInput(
                    hintRes = R.string.enter_pin_code,
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
                if (checkPinInput(pinCode.trim())) {
                    onSuccess()
                } else {
                    onFail()
                    wrongPinCode = true
                }
            }) {
                Text(
                    text = stringResource(R.string.enter),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onFail) {
                Text(
                    text = stringResource(R.string.close),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    )
}