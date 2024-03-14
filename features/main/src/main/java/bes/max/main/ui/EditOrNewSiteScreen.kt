package bes.max.main.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import bes.max.database.api.model.SiteInfoModel
import bes.max.main.presentation.edit.EditScreenState
import bes.max.main.presentation.edit.EditViewModel
import bes.max.main.ui.common.ShowError
import bes.max.main.ui.common.ShowLoading
import bes.max.main.ui.common.ShowTitle
import bes.max.main.ui.common.UserInput
import bes.max.passman.features.main.R
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import coil.memory.MemoryCache

@OptIn(ExperimentalCoilApi::class)
@Composable
fun EditOrNewSiteScreen(
    navigateBack: () -> Unit,
    launchAuth: (() -> Unit, () -> Unit) -> Unit,
    editViewModel: EditViewModel = hiltViewModel()
) {

    val uiState by editViewModel.uiState.observeAsState(initial = EditScreenState.Loading)
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    val isButtonEnabledForNew by remember {
        derivedStateOf {
            (name != "" && url != "" && newPassword != "")
        }
    }

    when (uiState) {
        is EditScreenState.Loading -> ShowLoading()

        is EditScreenState.Error -> ShowError(refresh = { })

        is EditScreenState.Edit -> ShowEdit(
            model = (uiState as EditScreenState.Edit).model,
            changeName = { name = it },
            changeUrl = { url = it },
            changePassword = { newPassword = it },
            showPassword = { model -> editViewModel.showPassword(model) },
            doEdit = {
                editViewModel.update(
                    (uiState as EditScreenState.Edit).model,
                    name,
                    url,
                    newPassword
                )
                navigateBack()
            },
            launchBiometric = launchAuth,
            deleteItem = { model ->
                editViewModel.delete(model)
                val imageLoader = context.imageLoader
                imageLoader.diskCache?.remove(url)
                imageLoader.memoryCache?.remove(MemoryCache.Key(url))
                navigateBack()
            }
        )

        is EditScreenState.New -> ShowNew(
            changeName = { name = it },
            changeUrl = { url = it },
            changePassword = { newPassword = it },
            create = {
                editViewModel.add(name, url, newPassword)
                navigateBack()
            },
            isButtonEnabled = isButtonEnabledForNew,
            launchBiometric = launchAuth,
        )
    }
}

@Composable
fun ShowEdit(
    model: SiteInfoModel,
    changeName: (String) -> Unit,
    changeUrl: (String) -> Unit,
    changePassword: (String) -> Unit,
    showPassword: (SiteInfoModel) -> String,
    doEdit: () -> Unit,
    launchBiometric: (() -> Unit, () -> Unit) -> Unit,
    deleteItem: (SiteInfoModel) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        ShowTitle(title = stringResource(id = R.string.edit))

        UserInput(
            hintRes = R.string.hint_name,
            initialText = model.name,
            onValueChanged = changeName
        )

        UserInput(hintRes = R.string.hint_url, initialText = model.url, onValueChanged = changeUrl)

        UserInput(
            hintRes = R.string.password,
            initialText = stringResource(id = R.string.hidden_text),
            onValueChanged = changePassword,
            passwordInput = true,
            showPassword = { showPassword(model) },
            launchBiometric = launchBiometric
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { launchBiometric(doEdit, { }) },
            ) {
                Text(
                    text = stringResource(id = R.string.edit),
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { launchBiometric({ deleteItem(model) }, { }) },
            ) {
                Text(
                    text = stringResource(id = R.string.delete),
                )
            }
        }
    }
}

@Composable
fun ShowNew(
    changeName: (String) -> Unit,
    changeUrl: (String) -> Unit,
    changePassword: (String) -> Unit,
    create: () -> Unit,
    isButtonEnabled: Boolean,
    launchBiometric: (() -> Unit, () -> Unit) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        ShowTitle(title = stringResource(id = R.string.add))

        UserInput(
            hintRes = R.string.hint_name,
            onValueChanged = changeName,
        )

        UserInput(
            hintRes = R.string.hint_url,
            initialText = stringResource(id = R.string.init_url),
            onValueChanged = changeUrl,
        )

        UserInput(
            hintRes = R.string.password,
            onValueChanged = changePassword,
        )

        Button(
            onClick = {
                launchBiometric({ create() }, { })

            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enabled = isButtonEnabled
        ) {
            Text(
                text = stringResource(id = R.string.add),
            )
        }
    }
}