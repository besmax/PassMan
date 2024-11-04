package bes.max.features.main.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import bes.max.features.main.presentation.settings.SettingsViewModel
import bes.max.features.main.ui.common.ShowTitle
import bes.max.passman.features.main.R

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {

    val isSystemPinCode by settingsViewModel.systemPinCode.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ShowTitle(title = stringResource(R.string.settings))

        Text(
            text = stringResource(R.string.use_pin_title),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            RadioButton(
                selected = isSystemPinCode,
                onClick = { },
            )

            Text(
                text = stringResource(R.string.system_pincode),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            RadioButton(
                selected = !isSystemPinCode,
                onClick = { },
            )

            Text(
                text = stringResource(R.string.app_pincode),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
        }
    }

}