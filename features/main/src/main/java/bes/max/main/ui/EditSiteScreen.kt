package bes.max.main.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import bes.max.database.api.model.SiteInfoModel
import bes.max.main.presentation.edit.EditScreenState
import bes.max.main.presentation.edit.EditViewModel
import bes.max.main.ui.common.ShowError
import bes.max.main.ui.common.ShowLoading
import bes.max.main.ui.common.UserInput
import bes.max.passman.features.main.R

@Composable
fun EditSiteScreen(
    editViewModel: EditViewModel = hiltViewModel()
) {

    val uiState by editViewModel.uiState.observeAsState(initial = EditScreenState.Loading)
    var name: String? = null
    var url: String? = null


    when (uiState) {
        is EditScreenState.Loading -> {
            ShowLoading()
        }

        is EditScreenState.Error -> {
            ShowError(refresh = { })
        }

        is EditScreenState.Edit -> {
            ShowEdit(
                model = (uiState as EditScreenState.Edit).model,
                changeName = { name = it },
                changeUrl = { url = it }
            )
        }

        is EditScreenState.New -> {
            ShowTitle(title = stringResource(id = R.string.title_new))

            TODO()
        }
    }
}

@Composable
fun ShowTitle(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 16.dp, top = 24.dp, bottom = 16.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun ShowEdit(
    model: SiteInfoModel,
    changeName: (String) -> Unit,
    changeUrl: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        ShowTitle(title = stringResource(id = R.string.title_edit))

        UserInput(
            hintRes = R.string.hint_name,
            initialText = model.name,
            onValueChanged = changeName
        )

        UserInput(hintRes = R.string.hint_url, initialText = model.url, onValueChanged = changeUrl)

    }
}