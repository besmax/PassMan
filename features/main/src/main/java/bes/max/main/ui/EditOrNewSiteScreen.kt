package bes.max.main.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import bes.max.database.api.model.SiteInfoModel
import bes.max.main.presentation.edit.EditScreenState
import bes.max.main.presentation.edit.EditViewModel
import bes.max.main.ui.common.ShowError
import bes.max.main.ui.common.ShowLoading
import bes.max.main.ui.common.ShowTitle
import bes.max.main.ui.common.UserInput
import bes.max.passman.features.main.R

@Composable
fun EditOrNewSiteScreen(
    navigateBack: () -> Unit,
    launchBiometric: (() -> Unit, () -> Unit) -> Unit,
    editViewModel: EditViewModel = hiltViewModel()
) {

    val uiState by editViewModel.uiState.observeAsState(initial = EditScreenState.Loading)
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

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
            launchBiometric = launchBiometric
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
            showPassword = { showPassword(model) }
        )

        Button(
            onClick = { doEdit() },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text(
                text = stringResource(id = R.string.edit),
            )
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
                launchBiometric({ create() }, {  })

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