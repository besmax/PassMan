package bes.max.features.main.ui

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bes.max.features.main.domain.models.FilterModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

const val CATEGORY_NAME_LENGTH = 10

@Composable
fun Categories(
    filters: ImmutableList<FilterModel>,
    addCategory: () -> Unit,
    addCategoryTitle: String,
    selected: Int,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        itemsIndexed(
            items = filters,
            key = { _, filter -> filter.color.toArgb() },
        ) { index, filter ->
            FilterChip(
                onClick = {
                    val unselect = selected == index
                    filter.filterAction(if (unselect) -1 else filter.color.toArgb())
                },
                label = {
                    Text(
                        text = filter.name?.take(CATEGORY_NAME_LENGTH) ?: "",
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
                    containerColor = filter.color,
                    selectedContainerColor = filter.color.copy(alpha = 0.6f),
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

@Preview
@Composable
private fun CategoriesPreview() {
    val model = FilterModel("filter1", Color.Blue, { })
    val filters = persistentListOf(
        model,
        model.copy(name = null, color = Color.Yellow),
        model.copy(name = "", color = Color.Red),
        model.copy(name = "filter4", color = Color.Green)
    )
    Categories(
        filters = filters,
        addCategory = {},
        addCategoryTitle = "Add",
        selected = 1
    )

}
