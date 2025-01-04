package bes.max.export.ui

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.os.Environment
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bes.max.export.R
import bes.max.export.ui.icon.folderIcon
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FileExplorerScreen(
    navigateBack: () -> Unit,
) {
    RequestPermission(
        permissions = setOf(READ_EXTERNAL_STORAGE),
        onNotGranted = navigateBack
    )

    var directory by remember { mutableStateOf(Environment.getExternalStorageDirectory().path) }
    val files = remember {
        derivedStateOf {
            File(directory).listFiles()?.toList() ?: emptyList()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn {
            items(files.value) { file ->
                if (file.isDirectory) {
                    FolderItem(
                        file = file,
                        onItemClick = {
                            directory = file.path
                        },
                    )
                } else {

                }
            }
        }
    }

}

@Composable
private fun FolderItem(
    file: File,
    onItemClick: () -> Unit,
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val creationDate = dateFormat.format(Date(file.lastModified()))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp)
            .clickable { onItemClick() }
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Icon(
                imageVector = folderIcon,
                contentDescription = stringResource(R.string.folder_explorer_item),
                modifier = Modifier,
            )

            Column(
                modifier = Modifier,
            ) {
                Text(
                    file.name
                )

                Text(
                    stringResource(
                        R.string.folder_explorer_item_date_count,
                        creationDate,
                        file.listFiles()?.size ?: 0
                    )
                )

            }

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = stringResource(R.string.folder_explorer_item),
                modifier = Modifier,
            )
        }

    }
}

@Preview
@Composable
private fun FolderItemPreview() {
    FolderItem(
        file = File(Environment.getExternalStorageDirectory().path),
        onItemClick = { },
    )
}
