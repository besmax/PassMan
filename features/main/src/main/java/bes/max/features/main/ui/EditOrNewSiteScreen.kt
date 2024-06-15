package bes.max.features.main.ui

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
import bes.max.features.main.domain.models.SiteInfoModelMain
import bes.max.features.main.presentation.edit.EditScreenState
import bes.max.features.main.presentation.edit.EditViewModel
import bes.max.features.main.ui.common.ShowError
import bes.max.features.main.ui.common.ShowLoading
import bes.max.features.main.ui.common.ShowTitle
import bes.max.features.main.ui.common.UserInput
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
    var comment by remember { mutableStateOf<String?>(null) }
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
            changeComment = { comment = it },
            showPassword = { model -> editViewModel.showPassword(model) },
            doEdit = {
                editViewModel.update(
                    (uiState as EditScreenState.Edit).model,
                    name,
                    url,
                    newPassword,
                    comment,
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
                editViewModel.add(name, url, newPassword, comment)
                navigateBack()
            },
            isButtonEnabled = isButtonEnabledForNew,
            launchBiometric = launchAuth,
            changeComment = { comment = it },
            showPassword = { newPassword }
        )
    }
}

@Composable
fun ShowEdit(
    model: SiteInfoModelMain,
    changeName: (String) -> Unit,
    changeUrl: (String) -> Unit,
    changePassword: (String) -> Unit,
    changeComment: (String?) -> Unit,
    showPassword: (SiteInfoModelMain) -> String,
    doEdit: () -> Unit,
    launchBiometric: (() -> Unit, () -> Unit) -> Unit,
    deleteItem: (SiteInfoModelMain) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        ShowTitle(title = stringResource(id = R.string.edit))

        UserInput(
            hintRes = R.string.hint_name,
            initialText = model.name,
            onValueChanged = changeName,
            maxLines = 3,
        )

        UserInput(
            hintRes = R.string.hint_url,
            initialText = model.url,
            onValueChanged = changeUrl,
            maxLines = 3,
        )

        UserInput(
            hintRes = R.string.password,
            initialText = model.password,
            onValueChanged = changePassword,
            passwordInput = true,
            showPassword = { showPassword(model) },
            launchBiometric = launchBiometric
        )

        UserInput(
            hintRes = R.string.comment,
            initialText = model.description ?: "",
            onValueChanged = changeComment,
            maxLines = 10,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { launchBiometric(doEdit, { }) },
            ) {
                Text(
                    text = stringResource(id = R.string.save),
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
    changeComment: (String) -> Unit,
    create: () -> Unit,
    isButtonEnabled: Boolean,
    launchBiometric: (() -> Unit, () -> Unit) -> Unit,
    showPassword: () -> String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        ShowTitle(title = stringResource(id = R.string.add))

        UserInput(
            hintRes = R.string.hint_name,
            onValueChanged = changeName,
            maxLines = 3,
        )

        UserInput(
            hintRes = R.string.hint_url,
            initialText = stringResource(id = R.string.init_url),
            onValueChanged = changeUrl,
            maxLines = 3,
        )

        UserInput(
            hintRes = R.string.password,
            onValueChanged = changePassword,
            passwordInput = true,
            showPassword = { showPassword() },
            launchBiometric = launchBiometric
        )

        UserInput(
            hintRes = R.string.comment,
            onValueChanged = changeComment,
            maxLines = 10,
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