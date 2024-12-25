package bes.max.features.main.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import bes.max.features.main.presentation.sites.SitesScreenState
import bes.max.features.main.ui.common.UserInput
import bes.max.features.main.ui.util.categoryColors
import bes.max.passman.features.main.R

@Composable
fun CategoryScreen(
    navigateBack: () -> Unit,
) {
    var name by remember { mutableStateOf("") }

    Content(
        changeName = { name = it },
    )

}

@Composable
private fun Content(
    changeName: (String) -> Unit,
) {
    var selected by remember { mutableIntStateOf(-1) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        UserInput(
            hintRes = R.string.hint_name,
            onValueChanged = changeName
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            itemsIndexed(
                items = categoryColors,
            ) { index, item ->
                ColorItem(
                    index = index,
                    color = item,
                    selected = selected,
                    onSelect = { selected = it }
                )
            }
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