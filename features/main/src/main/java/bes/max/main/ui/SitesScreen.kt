package bes.max.main.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bes.max.database.api.model.SiteInfoModel
import bes.max.main.presentation.SitesScreenState
import bes.max.main.presentation.SitesViewModel
import bes.max.passman.features.main.R
import coil.compose.AsyncImage


@Composable
fun SitesScreen(
    sitesViewModel: SitesViewModel = viewModel()
) {

    val uiState by sitesViewModel.uiState.observeAsState(SitesScreenState.Loading)

    when (uiState) {
        is SitesScreenState.Error -> ShowError(refresh = { })
        is SitesScreenState.Loading -> ShowLoading()
        is SitesScreenState.Content -> ShowContent(uiState as SitesScreenState.Content)
    }

}

@Composable
fun ShowContent(uiState: SitesScreenState.Content) {

}

@Composable
fun SiteListItem(
    model: SiteInfoModel,
    onItemClick: (Int) -> Unit,
    showPassword: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Gray,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 12.dp, end = 20.dp)
            .clickable { onItemClick(model.id) },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = model.iconUrl,
                placeholder = painterResource(R.drawable.logo_placeholder),
                contentDescription = "site logo"
            )
            Column() {
                Row {
                    Text(text = stringResource(id = R.string.site))
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    )
                    Text(text = model.name)
                }
            }
        }
    }

}

@Composable
@Preview
fun SiteListItemPreview() {
    SiteListItem(
        model = SiteInfoModel(
            1, "youtube", "qwerty123", "https://youtube.com", "https://youtube.com/favicon.ico",
        ),
        onItemClick = { },
        showPassword = { }
    )
}

@Composable
fun ShowLoading() {
    Column(
        modifier = Modifier
            .padding(top = 140.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(44.dp),
            color = Blue,
        )
    }
}

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
