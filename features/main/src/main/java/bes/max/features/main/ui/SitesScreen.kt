package bes.max.features.main.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.currentStateAsState
import bes.max.features.main.domain.models.FilterModel
import bes.max.features.main.domain.models.SiteInfoModelMain
import bes.max.features.main.presentation.sites.SitesScreenState
import bes.max.features.main.presentation.sites.SitesViewModel
import bes.max.features.main.ui.common.Categories
import bes.max.features.main.ui.common.LightGray
import bes.max.features.main.ui.common.ShowLoading
import bes.max.features.main.ui.common.ShowTitle
import bes.max.features.main.ui.common.UserInput
import bes.max.passman.features.main.R
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@Composable
fun SitesScreen(
    navigateToEdit: (Int) -> Unit,
    navigateToNew: () -> Unit,
    navigateToCategory: () -> Unit,
    launchAuth: (() -> Unit, () -> Unit) -> Unit,
    sitesViewModel: SitesViewModel = hiltViewModel(),
) {

    val uiState by sitesViewModel.uiState.observeAsState(SitesScreenState.Loading)
    val showPassword = { model: SiteInfoModelMain ->
        sitesViewModel.showPassword(model)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val state by lifecycleOwner.lifecycle.currentStateAsState()

    LaunchedEffect(key1 = state) {
        if (state == Lifecycle.State.STARTED) run {
            sitesViewModel.getSites()
        }
    }

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
                    is SitesScreenState.Empty -> ShowEmpty()
                    is SitesScreenState.Loading -> ShowLoading()
                    is SitesScreenState.Content -> ShowContent(
                        uiState as SitesScreenState.Content,
                        navigateToEdit,
                        showPassword,
                        launchAuth,
                        navigateToCategory
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
    showPassword: (SiteInfoModelMain) -> String,
    launchAuth: (() -> Unit, () -> Unit) -> Unit,
    navigateToCategory: () -> Unit,
) {
    SitesList(
        uiState.filteredSites,
        uiState.filters,
        onItemClick,
        showPassword,
        launchAuth,
        navigateToCategory
    )
}

@Composable
fun SitesList(
    list: List<SiteInfoModelMain>,
    filters: List<FilterModel>,
    onItemClick: (Int) -> Unit,
    showPassword: (SiteInfoModelMain) -> String,
    launchAuth: (() -> Unit, () -> Unit) -> Unit,
    navigateToCategory: () -> Unit,
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        var filterText by rememberSaveable {
            mutableStateOf("")
        }
        UserInput(
            hintRes = R.string.hint_sites_filter,
            initialText = filterText,
            onValueChanged = { filterText = it }
        )

        Categories(
            filters = filters,
            addCategory = navigateToCategory,
            addCategoryTitle = stringResource(R.string.add),
            modifier = Modifier,
        )

        LazyColumn(
            modifier = Modifier
                .padding(bottom = 12.dp, top = 8.dp)
        ) {
            val filteredList =
                if (filterText.isNotBlank()) list.filter { it.url.contains(filterText) }
                else list
            items(
                items = filteredList,
                key = { model -> model.id }
            ) { model ->
                SiteListItem(model, onItemClick, showPassword, launchAuth)
            }
        }
    }
}

@Composable
fun SiteListItem(
    model: SiteInfoModelMain,
    onItemClick: (Int) -> Unit,
    showPassword: (SiteInfoModelMain) -> String,
    launchAuth: (() -> Unit, () -> Unit) -> Unit,
) {

    var isPasswordVisible by rememberSaveable {
        mutableStateOf(false)
    }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 8.dp)
            .clickable { onItemClick(model.id) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 8.dp, end = 8.dp)
        ) {

            val context = LocalContext.current
            key(model) {
                Image(
                    rememberAsyncImagePainter(
                        remember(model.iconUrl) {
                            ImageRequest.Builder(context)
                                .data(model.iconUrl)
                                .diskCacheKey(model.iconUrl)
                                .memoryCacheKey(model.iconUrl)
                                .error(R.drawable.logo_placeholder)
                                .build()
                        }
                    ),
                    "site logo",
                    modifier = Modifier
                        .size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Row {
                Text(text = stringResource(id = R.string.site))

                Spacer(modifier = Modifier.width(8.dp))

                Text(text = model.name)

                if (model.categoryColor != null) {

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    )

                    Spacer(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = Color(model.categoryColor),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }

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
                    .clickable {
                        if (isPasswordVisible) {
                            isPasswordVisible = !isPasswordVisible
                        } else {
                            launchAuth({ isPasswordVisible = !isPasswordVisible }, {})
                        }
                    }
                    .size(24.dp)
            )
        }
    }
}

@Composable
fun ShowEmpty() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = stringResource(id = R.string.no_sites))
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

@Preview
@Composable
private fun SiteListItemPreview() {
    val model = SiteInfoModelMain(
        name = "NAME",
        password = "123234",
        passwordIv = "",
        url = "www.ww.w.v",
        description = null,
        categoryColor = Color.Red.toArgb()
    )
    SiteListItem(
        model = model,
        onItemClick = {},
        showPassword = { "" },
        launchAuth = { _, _ -> },
    )
}