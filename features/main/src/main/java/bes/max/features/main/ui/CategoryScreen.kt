package bes.max.features.main.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.rememberTooltipState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupPositionProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.currentStateAsState
import bes.max.features.main.domain.models.CategoryModelMain
import bes.max.features.main.presentation.category.CategoryScreenState
import bes.max.features.main.presentation.category.CategoryViewModel
import bes.max.ui.common.ShowLoading
import bes.max.ui.common.ShowTitle
import bes.max.ui.common.UserInput
import bes.max.passman.features.main.R

private const val CURRENT_CATEGORY_COLOR_SIZE = 24

@Composable
fun CategoryScreen(
    navigateBack: () -> Unit,
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }

    val uiState by categoryViewModel.uiState.observeAsState()

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
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
            navigateBack = navigateBack,
            deleteCategory = categoryViewModel::deleteCategory
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
    deleteCategory: (Int) -> Unit,
) {
    var selected by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ShowTitle(
            title = stringResource(R.string.categories),
            navigateBack = navigateBack,
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
            buttonEnabled = selected != -1,
            selected = selected
        )

        CurrentCategories(
            categories = state.categories,
            deleteCategory = deleteCategory
        )
    }
}

@Composable
private fun AddSection(
    colors: List<Color>,
    addCategory: () -> Unit,
    onSelect: (Int) -> Unit,
    selected: Int,
    modifier: Modifier = Modifier,
    buttonEnabled: Boolean = true,
) {
    if (colors.isNotEmpty()) {
        Colors(
            colors = colors,
            onSelect = onSelect,
            selected = selected
        )

        Button(
            onClick = {
                addCategory()
                onSelect(-1)
            },
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
    selected: Int,
) {
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

@Composable
private fun CurrentCategories(
    categories: List<CategoryModelMain>,
    deleteCategory: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    Text(
        text = stringResource(R.string.current_categories),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp),
        style = MaterialTheme.typography.titleMedium
    )

    if (categories.isEmpty()) {
        Text(
            text = stringResource(R.string.no_categories),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 4.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        items(
            items = categories,
            key = { model -> model.color.toArgb() }
        ) { model ->
            CurrentCategoryItem(
                item = model,
                deleteCategory = { color -> deleteCategory(color) },
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrentCategoryItem(
    item: CategoryModelMain,
    deleteCategory: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tooltipState = rememberTooltipState()

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val deleteContentDescr = stringResource(R.string.delete_category)

            Spacer(modifier = Modifier.width(8.dp))

            Spacer(
                modifier = Modifier
                    .size(CURRENT_CATEGORY_COLOR_SIZE.dp)
                    .background(
                        color = item.color,
                        shape = RoundedCornerShape(4.dp)
                    )
            )

            Text(
                text = item.name,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            )

            Spacer(modifier.weight(1f))

            TooltipBox(
                positionProvider = tooltipPositionProvider,
                tooltip = {
                    Text(deleteContentDescr)
                },
                state = tooltipState,
                modifier = Modifier
            ) {
                IconButton(
                    onClick = { deleteCategory(item.color.toArgb()) },
                    modifier = Modifier
                        .padding(end = 16.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = deleteContentDescr
                    )
                }
            }
        }
    }

}

internal val tooltipPositionProvider = object : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        return IntOffset(anchorBounds.left, anchorBounds.top - popupContentSize.height)
    }

}

@Composable
@Preview
private fun CurrentCategoryItemPreview() {
    val item = CategoryModelMain("COLOR", Color.Yellow)
    CurrentCategoryItem(
        item = item,
        deleteCategory = {},
    )
}