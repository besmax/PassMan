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
import bes.max.ui.common.UserInputStateLess
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
    val name by editViewModel.name.collectAsState()
    val url by editViewModel.url.collectAsState()
    val comment by editViewModel.comment.collectAsState()
    val newPassword by editViewModel.password.collectAsState()
    val categoryColor by editViewModel.color.collectAsState()
    val login by editViewModel.login.collectAsState()
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val wrongPinCodeText = stringResource(R.string.wrong_pin_code)

    val isButtonEnabledForNew by remember {
        derivedStateOf {
            (name != "" && newPassword.password != "")
        }
    }
    val isButtonEnabledForEdit by remember {
        derivedStateOf {
            (uiState as? EditScreenState.Edit)?.model?.anyFieldChange(
                name,
                url,
                newPassword.password,
                comment,
                categoryColor,
                login
            )
                ?: false
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
                            contentDescription = stringResource(R.string.go_back_icon),
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
                        name = name,
                        url = url,
                        password = newPassword.password,
                        comment = comment,
                        color = categoryColor,
                        login = login,
                        changeName = editViewModel::onNameChanged,
                        changeUrl = editViewModel::onUrlChanged,
                        changePassword = editViewModel::onPasswordChanged,
                        changeComment = editViewModel::onCommentChanged,
                        showPassword = { model -> editViewModel.showPassword(model) },
                        changeLogin = editViewModel::onLoginChanged,
                        doEdit = {
                            editViewModel.update(state.model)
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
                        changeCategory = editViewModel::onCategoryChanged,
                        navigateToCategory = navigateToCategory,
                        isButtonEnabled = isButtonEnabledForEdit
                    )

                    is EditScreenState.New -> ShowNew(
                        name = name,
                        url = url,
                        password = newPassword.password,
                        comment = comment,
                        color = categoryColor,
                        login = login,
                        changeName = editViewModel::onNameChanged,
                        changeUrl = editViewModel::onUrlChanged,
                        changePassword = editViewModel::onPasswordChanged,
                        create = {
                            editViewModel.add()
                            navigateBack()
                        },
                        isButtonEnabled = isButtonEnabledForNew,
                        launchBiometric = authentication,
                        changeComment = editViewModel::onCommentChanged,
                        showPassword = { editViewModel.showPassword() },
                        changeLogin = editViewModel::onLoginChanged,
                        categories = (uiState as EditScreenState.New).categories,
                        changeCategory = editViewModel::onCategoryChanged,
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
    name: String,
    url: String,
    password: String,
    comment: String,
    color: Int?,
    login: String,
    changeName: (String) -> Unit,
    changeUrl: (String) -> Unit,
    changePassword: (String) -> Unit,
    changeComment: (String?) -> Unit,
    showPassword: (SiteInfoModelMain) -> Unit,
    changeLogin: (String?) -> Unit,
    doEdit: () -> Unit,
    launchBiometric: (() -> Unit, () -> Unit) -> Unit,
    deleteItem: (SiteInfoModelMain) -> Unit,
    categories: List<CategoryModelMain>,
    changeCategory: (Int?) -> Unit,
    navigateToCategory: () -> Unit,
    isButtonEnabled: Boolean,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        UserInputStateLess(
            hintRes = R.string.hint_name,
            text = name,
            onValueChanged = changeName,
            maxLines = 3,
            clearText = {
                changeName("")
            }
        )

        UserInputStateLess(
            hintRes = R.string.hint_url,
            text = url,
            onValueChanged = changeUrl,
            maxLines = 3,
            clearText = {
                changeUrl("")
            }
        )

        UserInputStateLess(
            hintRes = R.string.login,
            text = login,
            onValueChanged = changeLogin,
            maxLines = 3,
            clearText = {
                changeLogin("")
            }
        )

        UserInputStateLess(
            hintRes = R.string.password,
            text = password,
            onValueChanged = changePassword,
            passwordInput = true,
            showPassword = { showPassword(model) },
            launchBiometric = launchBiometric
        )

        UserInputStateLess(
            hintRes = R.string.comment,
            text = comment,
            onValueChanged = changeComment,
            maxLines = 10,
            clearText = {
                changeComment("")
            }
        )

        Text(
            text = stringResource(id = R.string.choose_category),
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.titleMedium
        )

        ChooseCategory(
            categories = categories,
            changeCategory = changeCategory,
            categoryColor = color ?: -1,
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
                enabled = isButtonEnabled
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
    name: String,
    url: String,
    password: String,
    comment: String,
    color: Int?,
    login: String,
    changeName: (String) -> Unit,
    changeUrl: (String) -> Unit,
    changePassword: (String) -> Unit,
    changeComment: (String) -> Unit,
    create: () -> Unit,
    isButtonEnabled: Boolean,
    launchBiometric: (() -> Unit, () -> Unit) -> Unit,
    showPassword: () -> Unit,
    changeLogin: (String) -> Unit,
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
        UserInputStateLess(
            hintRes = R.string.hint_name,
            text = name,
            onValueChanged = changeName,
            maxLines = 3,
            clearText = {
                changeName("")
            }
        )

        UserInputStateLess(
            hintRes = R.string.hint_url,
            text = url,
            onValueChanged = changeUrl,
            maxLines = 3,
            clearText = {
                changeUrl("")
            }
        )

        UserInputStateLess(
            hintRes = R.string.login,
            text = login,
            onValueChanged = changeLogin,
            maxLines = 3,
            clearText = {
                changeLogin("")
            }
        )

        UserInputStateLess(
            hintRes = R.string.password,
            text = password,
            onValueChanged = changePassword,
            passwordInput = true,
            showPassword = { showPassword() },
            launchBiometric = launchBiometric
        )

        UserInputStateLess(
            hintRes = R.string.comment,
            text = comment,
            onValueChanged = changeComment,
            maxLines = 10,
            clearText = {
                changeComment("")
            }
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
            categoryColor = color ?: -1,
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
                            contentDescription = stringResource(R.string.chosen_filter),
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
                        contentDescription = stringResource(R.string.add_filter),
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

private fun SiteInfoModelMain.anyFieldChange(
    name: String,
    url: String,
    password: String,
    comment: String,
    color: Int?,
    login: String,
): Boolean {
    if (this.name != name) return true
    if (this.url != url) return true
    if (this.password != password) return true
    if ((this.description ?: "") != comment) return true
    if (this.categoryColor != color) return true
    if ((this.login ?: "") != login) return true
    return false
}