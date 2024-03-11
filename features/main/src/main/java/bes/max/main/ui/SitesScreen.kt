package bes.max.main.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import bes.max.database.api.model.SiteInfoModel
import bes.max.main.presentation.SitesScreenState
import bes.max.main.presentation.SitesViewModel
import bes.max.passman.features.main.R
import coil.compose.AsyncImage


@Composable
fun SitesScreen(
    sitesViewModel: SitesViewModel = hiltViewModel()
) {

    val uiState by sitesViewModel.uiState.observeAsState(SitesScreenState.Loading)
    val onItemClick: (Int) -> Unit = { }
    val showPassword = { model: SiteInfoModel ->
        sitesViewModel.showPassword(model)
    }
    val refresh: () -> Unit = sitesViewModel::getSites

    when (uiState) {
        is SitesScreenState.Error -> ShowError(refresh = refresh)
        is SitesScreenState.Loading -> ShowLoading()
        is SitesScreenState.Content -> ShowContent(
            uiState as SitesScreenState.Content,
            onItemClick,
            showPassword,
        )
    }

}

@Composable
fun ShowContent(
    uiState: SitesScreenState.Content,
    onItemClick: (Int) -> Unit,
    showPassword: (SiteInfoModel) -> String,
) {
    if (uiState.sites.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = stringResource(id = R.string.no_sites))
        }
    } else {
        SitesList(uiState.sites, onItemClick, showPassword)
    }
}

@Composable
fun SitesList(
    list: List<SiteInfoModel>,
    onItemClick: (Int) -> Unit,
    showPassword: (SiteInfoModel) -> String,
) {
    LazyColumn(
        modifier = Modifier
            .padding(vertical = 12.dp)
    ) {
        items(
            items = list,
            key = { model -> model.id }
        ) { model ->
            SiteListItem(model, onItemClick, showPassword)
        }
    }
}


@Composable
fun SiteListItem(
    model: SiteInfoModel,
    onItemClick: (Int) -> Unit,
    showPassword: (SiteInfoModel) -> String,
) {

    var isPasswordVisible by rememberSaveable {
        mutableStateOf(false)
    }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Gray,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 8.dp, end = 8.dp)
            .clickable { onItemClick(model.id) },
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = model.iconUrl,
                placeholder = painterResource(R.drawable.logo_placeholder),
                error = painterResource(R.drawable.logo_placeholder),
                contentDescription = "site logo",
                modifier = Modifier
                    .size(80.dp)
            )
            Column(
                modifier = Modifier
                    .padding(vertical = 8.dp)
            ) {
                Row {
                    Text(text = stringResource(id = R.string.site))
                    Spacer(
                        modifier = Modifier
                            .width(16.dp),
                    )
                    Text(text = model.name)
                }

                Spacer(modifier = Modifier.width(24.dp))

                Row {
                    Text(text = stringResource(id = R.string.password))
                    Spacer(
                        modifier = Modifier
                            .width(16.dp),
                    )
                    Text(
                        text = if (isPasswordVisible) {
                            showPassword(model)
                        } else stringResource(id = R.string.hidden_text)
                    )
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    )
                    Icon(
                        painter = painterResource(
                            id = if (isPasswordVisible) (R.drawable.hide_icon)
                            else R.drawable.show_icon
                        ),
                        contentDescription = "Show password icon",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { isPasswordVisible = !isPasswordVisible }
                    )
                }
            }
        }
    }
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
