package bes.max.features.main.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.currentStateAsState
import bes.max.features.main.domain.models.FilterModel
import bes.max.features.main.domain.models.SiteInfoModelMain
import bes.max.features.main.presentation.settings.SettingsViewModel
import bes.max.features.main.presentation.sites.SitesScreenEvent
import bes.max.features.main.presentation.sites.SitesScreenState
import bes.max.features.main.presentation.sites.SitesViewModel
import bes.max.features.main.ui.icon.copyIcon
import bes.max.features.main.ui.icon.globeIcon
import bes.max.features.main.ui.icon.lockIcon
import bes.max.features.main.ui.icon.settingsIcon
import bes.max.passman.features.main.R
import bes.max.ui.common.AnimatedBackground
import bes.max.ui.common.Information
import bes.max.ui.common.ShowLoading
import bes.max.ui.common.UserInput
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SitesScreen(
    navigateToEdit: (Int) -> Unit,
    navigateToNew: () -> Unit,
    navigateToCategory: () -> Unit,
    navigateToSettings: () -> Unit,
    launchAuth: (() -> Unit, () -> Unit) -> Unit,
    sitesViewModel: SitesViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {

    val uiState by sitesViewModel.uiState.observeAsState(SitesScreenState.Loading)
    val event by sitesViewModel.event.observeAsState()
    val pinCode by settingsViewModel.pinCode.collectAsState()
    val isAnimBackgroundActive by settingsViewModel.isAnimBackgroundActive.collectAsState()
    val showPassword = { model: SiteInfoModelMain ->
        sitesViewModel.showPassword(model)
    }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val wrongPinCodeText = stringResource(R.string.wrong_pin_code)
    val copiedText = stringResource(R.string.copied)


    val copyPasswordToClipboard = { model: SiteInfoModelMain ->
        scope.launch {
            snackbarHostState.showSnackbar(copiedText)
        }
        sitesViewModel.copyPasswordToClipboard(model)
    }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val state by lifecycleOwner.lifecycle.currentStateAsState()

    var showPinCodeInput by remember { mutableStateOf(false) }

    var authOnSuccess by remember { mutableStateOf({ }) }
    var authOnFail by remember { mutableStateOf({ }) }

    val authentication: (() -> Unit, () -> Unit) -> Unit = { onSuccess, onFail ->
        if (pinCode?.active == true) {
            authOnSuccess = {
                onSuccess()
                showPinCodeInput = false
            }
            authOnFail = {
                onFail()
                scope.launch {
                    snackbarHostState.showSnackbar(wrongPinCodeText)
                }
                showPinCodeInput = false
            }
            showPinCodeInput = true
        } else {
            launchAuth(onSuccess, onFail)
        }
    }

    LaunchedEffect(key1 = state) {
        if (state == Lifecycle.State.STARTED) run {
            sitesViewModel.getSites()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            FabAdd(navigateToNew)
        },
        topBar = {
            TopAppBar(
                title = {
                    if (uiState is SitesScreenState.Content && (uiState as SitesScreenState.Content).selected != 0) {
                        Text(
                            text = stringResource(
                                id = R.string.chosen,
                                (uiState as SitesScreenState.Content).selected.toString()
                            ),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.saved_passwords),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                },
                actions = {
                    if (uiState is SitesScreenState.Content && (uiState as SitesScreenState.Content).selected != 0) {
                        IconButton(
                            onClick = { launchAuth(sitesViewModel::deleteSelected, {}) },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete chosen items icon",
                                tint = MaterialTheme.colorScheme.onBackground,
                            )
                        }

                        IconButton(
                            onClick = sitesViewModel::unselectAll,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Unselect selected items icon",
                                tint = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    } else {
                        IconButton(
                            onClick = navigateToSettings,
                        ) {
                            Icon(
                                imageVector = settingsIcon,
                                contentDescription = "Go to settings icon",
                                tint = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                Crossfade(
                    targetState = uiState,
                    animationSpec = tween(durationMillis = 600),
                    label = "Sites Screen States Changes"
                ) { state ->
                   if (isAnimBackgroundActive) {
                        AnimatedBackground(
                            animIcon = lockIcon,
                            modifier = Modifier,
                            backgroundColor = Color.Transparent,
                            iconColor = Color.Gray,
                            accelerationProgress = false,
                        )
                    }
                    when (state) {
                        is SitesScreenState.Empty -> ShowEmpty()
                        is SitesScreenState.Loading -> ShowLoading()
                        is SitesScreenState.Content -> ShowContent(
                            uiState = state,
                            onItemClick = navigateToEdit,
                            showPassword = showPassword,
                            launchAuth = authentication,
                            navigateToCategory = navigateToCategory,
                            copyPasswordToClipboard = copyPasswordToClipboard,
                            onItemLongClick = sitesViewModel::toggleItemSelection,
                            isSelecting = state.selected != 0,
                            openUrl = sitesViewModel::openUrlInBrowser
                        )
                    }
                }

                if (showPinCodeInput) {
                    CheckPinCode(
                        onSuccess = authOnSuccess,
                        onFail = authOnFail,
                        checkPinInput = settingsViewModel::checkInputPinCode
                    )
                }

                ShowEvent(
                    event = event,
                    onDismiss = sitesViewModel::resetEvent,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
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
    copyPasswordToClipboard: (SiteInfoModelMain) -> Unit,
    onItemLongClick: (Int) -> Unit,
    isSelecting: Boolean,
    openUrl: (String) -> Unit,
) {
    SitesList(
        uiState.filteredSites,
        uiState.filters,
        onItemClick,
        showPassword,
        launchAuth,
        navigateToCategory,
        copyPasswordToClipboard,
        onItemLongClick,
        isSelecting,
        openUrl
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
    copyPasswordToClipboard: (SiteInfoModelMain) -> Unit,
    onItemLongClick: (Int) -> Unit,
    isSelecting: Boolean,
    openUrl: (String) -> Unit,
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
            addCategoryTitle = stringResource(R.string.add_category),
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
                SiteListItem(
                    model,
                    onItemClick,
                    showPassword,
                    copyPasswordToClipboard,
                    launchAuth,
                    onItemLongClick,
                    isSelecting,
                    openUrl
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SiteListItem(
    model: SiteInfoModelMain,
    onItemClick: (Int) -> Unit,
    showPassword: (SiteInfoModelMain) -> String,
    copyPasswordToClipboard: (SiteInfoModelMain) -> Unit,
    launchAuth: (() -> Unit, () -> Unit) -> Unit,
    onItemLongClick: (Int) -> Unit,
    isSelecting: Boolean,
    openUrl: (String) -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {

    var isPasswordVisible by rememberSaveable {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (model.isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceContainer,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 8.dp)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { if (isSelecting) onItemLongClick(model.id) else onItemClick(model.id) },
                onLongClick = { onItemLongClick(model.id) }
            ),
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
                Text(text = stringResource(id = R.string.name))

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
                imageVector = globeIcon,
                contentDescription = "Go to web-site icon",
                modifier = Modifier
                    .clickable {
                        openUrl(model.url)
                    }
                    .size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Icon(
                imageVector = copyIcon,
                contentDescription = "Copy password icon",
                modifier = Modifier
                    .clickable {
                        launchAuth({ copyPasswordToClipboard(model) }, {})
                    }
                    .size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

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
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.nothing),
            contentDescription = stringResource(id = R.string.no_sites),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
        )

        Spacer(Modifier.height(16.dp))

        Text(text = stringResource(id = R.string.no_sites))
    }
}

@Composable
fun FabAdd(addItem: () -> Unit) {
    FloatingActionButton(
        onClick = { addItem() },
        modifier = Modifier
            .padding(end = 16.dp, bottom = 80.dp),
        shape = RoundedCornerShape(100.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add icon",
        )
    }
}

@Composable
private fun ShowEvent(
    event: SitesScreenEvent?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Crossfade(event, label = "ShowEvent") { ev ->
        when (ev) {
            is SitesScreenEvent.WrongUrl -> Information(
                text = stringResource(ev.messageResId),
                modifier = modifier,
                onDismiss = onDismiss
            )
            else -> return@Crossfade
        }
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
        copyPasswordToClipboard = {},
        launchAuth = { _, _ -> },
        onItemLongClick = {},
        isSelecting = false,
        openUrl = {},
    )
}