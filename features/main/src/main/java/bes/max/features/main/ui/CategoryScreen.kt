package bes.max.features.main.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.currentStateAsState
import bes.max.features.main.presentation.category.CategoryScreenState
import bes.max.features.main.presentation.category.CategoryViewModel
import bes.max.features.main.presentation.sites.SitesScreenState
import bes.max.features.main.ui.common.ShowLoading
import bes.max.features.main.ui.common.ShowTitle
import bes.max.features.main.ui.common.UserInput
import bes.max.features.main.ui.util.categoryColors
import bes.max.passman.features.main.R

@Composable
fun CategoryScreen(
    navigateBack: () -> Unit,
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }

    val uiState by categoryViewModel.uiState.observeAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    val state by lifecycleOwner.lifecycle.currentStateAsState()

    LaunchedEffect(key1 = state) {
        if (state == Lifecycle.State.STARTED) run {
            categoryViewModel.getCategories()
        }
    }

    when (uiState) {
        is CategoryScreenState.Content -> Content(
            state = uiState as CategoryScreenState.Content,
            changeName = { name = it },
            addCategory = { colorIndex ->
                categoryViewModel.addCategory(
                    (uiState as CategoryScreenState.Content).colors[colorIndex],
                    name
                )
            },
            navigateBack = navigateBack
        )

        is CategoryScreenState.Loading -> ShowLoading()
    }

}

@Composable
private fun Content(
    state: CategoryScreenState.Content,
    changeName: (String) -> Unit,
    addCategory: (Int) -> Unit,
    navigateBack: () -> Unit,
) {
    var selected by remember { mutableIntStateOf(-1) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ShowTitle(
            title = stringResource(R.string.categories),
            goBack = navigateBack,
        )

        UserInput(
            hintRes = R.string.hint_name,
            onValueChanged = changeName
        )

        Spacer(modifier = Modifier.height(8.dp))

        AddSection(
            colors = state.colors,
            addCategory = { addCategory(selected) },
            onSelect = { color -> selected = color },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            buttonEnabled = selected != -1
        )
    }
}

@Composable
private fun AddSection(
    colors: List<Color>,
    addCategory: () -> Unit,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    buttonEnabled: Boolean = true,
) {
    if (colors.isNotEmpty()) {
        Colors(
            colors = colors,
            onSelect = onSelect
        )

        Button(
            onClick = addCategory,
            modifier = modifier,
            enabled = buttonEnabled,
        ) {
            Text(text = stringResource(R.string.add))
        }
    } else {
        Text(
            text = stringResource(R.string.categories_occupied)
        )
    }
}

@Composable
private fun Colors(
    colors: List<Color>,
    onSelect: (Int) -> Unit,
) {
    var selected by remember { mutableIntStateOf(-1) }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        itemsIndexed(
            items = colors,
        ) { index, item ->
            ColorItem(
                index = index,
                color = item,
                selected = selected,
                onSelect = {
                    selected = it
                    onSelect(it)
                }
            )
        }
    }
}

@Composable
private fun ColorItem(
    index: Int,
    color: Color,
    selected: Int,
    onSelect: (Int) -> Unit
) {
    FilterChip(
        onClick = {
            onSelect(index)
        },
        label = {},
        selected = selected == index,
        leadingIcon = if (selected == index) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Color is chosen",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
        modifier = Modifier.defaultMinSize(minWidth = 36.dp),
        colors = FilterChipDefaults.filterChipColors().copy(
            containerColor = color,
            selectedContainerColor = color,
        )
    )
}