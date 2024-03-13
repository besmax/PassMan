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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import bes.max.database.api.model.SiteInfoModel
import bes.max.main.presentation.sites.SitesScreenState
import bes.max.main.presentation.sites.SitesViewModel
import bes.max.main.ui.common.LightGray
import bes.max.main.ui.common.ShowError
import bes.max.main.ui.common.ShowLoading
import bes.max.main.ui.common.ShowTitle
import bes.max.passman.features.main.R
import coil.compose.AsyncImage


@Composable
fun SitesScreen(
    navigateToEdit: (Int) -> Unit,
    navigateToNew: () -> Unit,
    sitesViewModel: SitesViewModel = hiltViewModel(),
) {

    val context = LocalContext.current

    val uiState by sitesViewModel.uiState.observeAsState(SitesScreenState.Loading)
    val showPassword = { model: SiteInfoModel ->
        sitesViewModel.showPassword(model)
    }
    val refresh: () -> Unit = sitesViewModel::getSites

    Scaffold(
        floatingActionButton = { FabAdd(navigateToNew) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                ShowTitle(title = stringResource(id = R.string.saved_passwords))

                when (uiState) {
                    is SitesScreenState.Error -> ShowError(refresh = refresh)
                    is SitesScreenState.Loading -> ShowLoading()
                    is SitesScreenState.Content -> ShowContent(
                        uiState as SitesScreenState.Content,
                        navigateToEdit,
                        showPassword,
                    )
                }
            }
        }
    )


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
            .padding(bottom = 12.dp)
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
            containerColor = LightGray,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 8.dp, end = 8.dp)
            .clickable { onItemClick(model.id) },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 8.dp, end = 8.dp)
        ) {
            AsyncImage(
                model = model.iconUrl,
                placeholder = painterResource(R.drawable.logo_placeholder),
                error = painterResource(R.drawable.logo_placeholder),
                contentDescription = "site logo",
                modifier = Modifier
                    .size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Row {
                Text(text = stringResource(id = R.string.site))

                Spacer(modifier = Modifier.width(8.dp))

                Text(text = model.name)
            }

            Spacer(modifier = Modifier.width(24.dp))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 8.dp)
        ) {

            Text(text = stringResource(id = R.string.password))

            Spacer(modifier = Modifier.width(16.dp))

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
                    .clickable { isPasswordVisible = !isPasswordVisible }
                    .size(24.dp)
            )

        }
    }
}

@Composable
fun FabAdd(addItem: () -> Unit) {
    FloatingActionButton(
        onClick = { addItem() },
        modifier = Modifier
            .padding(end = 16.dp, bottom = 24.dp),
        shape = RoundedCornerShape(100.dp),
        containerColor = LightGray,
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add icon",
        )
    }
}