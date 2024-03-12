package bes.max.main.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import bes.max.passman.features.main.R

@Composable
fun ShowError(
    message: String? = null,
    refresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(top = 140.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message ?: stringResource(id = R.string.error)
        )
        Button(onClick = { refresh() }) {
            Text(text = stringResource(id = R.string.refresh))
        }
    }
}