package bes.max.features.main.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import bes.max.features.main.domain.models.CategoryModelMain
import bes.max.features.main.domain.models.SiteInfoModelMain
import bes.max.features.main.presentation.edit.EditScreenState
import bes.max.features.main.presentation.edit.EditViewModel
import bes.max.features.main.presentation.settings.SettingsViewModel
import bes.max.passman.features.main.R
import bes.max.ui.common.ShowError
import bes.max.ui.common.ShowLoading
import bes.max.ui.common.UserInput
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import coil.memory.MemoryCache
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoilApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditOrNewSiteScreen(
    navigateBack: () -> Unit,
    launchAuth: (() -> Unit, () -> Unit) -> Unit,
    navigateToCategory: () -> Unit,
    editViewModel: EditViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {

    val uiState by editViewModel.uiState.observeAsState(initial = EditScreenState.Loading)
    var name by rememberSaveable { mutableStateOf("") }
    var url by rememberSaveable { mutableStateOf("") }
    var comment by rememberSaveable { mutableStateOf<String?>(null) }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var categoryColor by rememberSaveable { mutableStateOf<Int?>(null) }
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val wrongPinCodeText = stringResource(R.string.wrong_pin_code)

    val isButtonEnabledForNew by remember {
        derivedStateOf {
            (name != "" && newPassword != "")
        }
    }
    var showPinCodeInput by remember { mutableStateOf(false) }
    val pinCode by settingsViewModel.pinCode.collectAsState()
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

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            id = if (uiState is EditScreenState.Edit) R.string.edit
                            else R.string.add
                        ),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navigateBack() }
                    )
                },
                navigationIcon = {
                    IconButton(navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back icon",
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Crossfade(
                targetState = uiState,
                animationSpec = tween(durationMillis = 600),
                label = "Sites Screen States Changes",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) { state ->
                when (state) {
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
                                state.model,
                                name,
                                url,
                                newPassword,
                                comment,
                                categoryColor
                            )
                            navigateBack()
                        },
                        launchBiometric = authentication,
                        deleteItem = { model ->
                            editViewModel.delete(model)
                            val imageLoader = context.imageLoader
                            imageLoader.diskCache?.remove(url)
                            imageLoader.memoryCache?.remove(MemoryCache.Key(url))
                            navigateBack()
                        },
                        categories = (uiState as EditScreenState.Edit).categories,
                        changeCategory = { categoryColor = it },
                        navigateToCategory = navigateToCategory
                    )

                    is EditScreenState.New -> ShowNew(
                        changeName = { name = it },
                        changeUrl = { url = it },
                        changePassword = { newPassword = it },
                        create = {
                            editViewModel.add(name, url, newPassword, comment, categoryColor)
                            navigateBack()
                        },
                        isButtonEnabled = isButtonEnabledForNew,
                        launchBiometric = authentication,
                        changeComment = { comment = it },
                        showPassword = { newPassword },
                        categories = (uiState as EditScreenState.New).categories,
                        changeCategory = { categoryColor = it },
                        navigateToCategory = navigateToCategory
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
        }
    )

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
    categories: List<CategoryModelMain>,
    changeCategory: (Int?) -> Unit,
    navigateToCategory: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
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

        Text(
            text = stringResource(id = R.string.choose_category),
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.titleMedium
        )

        ChooseCategory(
            categories = categories,
            changeCategory = changeCategory,
            categoryColor = model.categoryColor ?: -1,
            addCategory = navigateToCategory,
            addCategoryTitle = stringResource(R.string.add_category),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
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
    showPassword: () -> String,
    categories: List<CategoryModelMain>,
    changeCategory: (Int?) -> Unit,
    navigateToCategory: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
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

        Text(
            text = stringResource(id = R.string.choose_category),
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.titleMedium,
        )

        ChooseCategory(
            categories = categories,
            changeCategory = changeCategory,
            addCategory = navigateToCategory,
            addCategoryTitle = stringResource(R.string.add_category),
        )

        Button(
            onClick = {
                launchBiometric({ create() }, { })

            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp),
            enabled = isButtonEnabled
        ) {
            Text(
                text = stringResource(id = R.string.add),
            )
        }
    }
}

@Composable
private fun ChooseCategory(
    categories: List<CategoryModelMain>,
    changeCategory: (Int?) -> Unit,
    addCategory: () -> Unit,
    addCategoryTitle: String,
    categoryColor: Int = -1,
    modifier: Modifier = Modifier,
) {
    val selectedIndex = categories.indexOfFirst { it.color.toArgb() == categoryColor }
    var selected by remember { mutableIntStateOf(selectedIndex) }

    LazyRow(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        itemsIndexed(
            items = categories
        ) { index, category ->
            FilterChip(
                onClick = {
                    selected = if (selected == index) {
                        changeCategory(null)
                        -1
                    } else {
                        changeCategory(category.color.toArgb())
                        index
                    }
                },
                label = {
                    Text(
                        text = category.name.take(CATEGORY_NAME_LENGTH),
                        color = Color.White,
                    )
                },
                selected = selected == index,
                leadingIcon = if (selected == index) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Filter is chosen",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
                modifier = Modifier
                    .defaultMinSize(minWidth = 36.dp)
                    .padding(end = 8.dp),
                colors = FilterChipDefaults.filterChipColors().copy(
                    containerColor = category.color,
                    selectedContainerColor = category.color.copy(alpha = 0.6f),
                )
            )
        }

        item {
            FilterChip(
                onClick = { addCategory() },
                label = {
                    Text(
                        text = addCategoryTitle,
                        color = Color.Black,
                    )
                },
                selected = false,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add filter",
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                },
                colors = FilterChipDefaults.filterChipColors().copy(
                    containerColor = Color.White,
                    selectedContainerColor = Color.White,
                )
            )
        }
    }
}