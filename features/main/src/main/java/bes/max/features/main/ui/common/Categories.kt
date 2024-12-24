package bes.max.features.main.ui.common

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bes.max.features.main.domain.models.FilterModel

@Composable
fun Categories(
    filters: List<FilterModel>,
    modifier: Modifier = Modifier,
) {
    var selected by remember { mutableIntStateOf(-1) }

    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        filters.forEachIndexed() { index, filter ->
            FilterChip(
                onClick = {
                    selected = index
                    filter.filterAction(filter.color.toArgb())
                },
                label = {
                    Text(
                        text = filter.name ?: "",
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
                modifier = Modifier.defaultMinSize(minWidth = 36.dp),
                colors = FilterChipDefaults.filterChipColors().copy(
                    containerColor = filter.color.copy(alpha = 0.3f),
                    selectedContainerColor = filter.color,
                )
            )
        }

    }
}

@Preview
@Composable
private fun CategoriesPreview() {
    val model = FilterModel("filter1", Color.Blue, { })
    val filters = mutableListOf(
        model,
        model.copy(name = null, color = Color.Yellow),
        model.copy(name = "", color = Color.Red),
        model.copy(name = "filter4", color = Color.Green),
    )
    Categories(filters)
}
