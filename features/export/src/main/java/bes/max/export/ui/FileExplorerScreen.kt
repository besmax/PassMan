package bes.max.export.ui

import android.os.Environment
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bes.max.export.R
import bes.max.export.ui.icon.audioFileIcon
import bes.max.export.ui.icon.fileIcon
import bes.max.export.ui.icon.folderIcon
import bes.max.export.ui.icon.imageFileIcon
import bes.max.export.ui.icon.jsonFileIcon
import bes.max.export.ui.icon.tablesFileIcon
import bes.max.export.ui.icon.textFileIcon
import bes.max.export.ui.icon.videoFileIcon
import bes.max.export.util.getFormattedFileSize
import bes.max.ui.common.ShowTitle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val DEFAULT_NAME_LENGTH = 15

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FileExplorerScreen(
    navigateBack: () -> Unit,
) {
//    val multiplePermissionsState = rememberMultiplePermissionsState(getPermissions())
//
//    val requestPermissionsLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    ) { permissions ->
//        // Handle permission results
//    }
//
//    LaunchedEffect(Unit) {
//        if (!multiplePermissionsState.allPermissionsGranted) {
//            requestPermissionsLauncher.launch(arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE))
//            Log.e("TAAAAAAAG", "launcher=$requestPermissionsLauncher granted=${multiplePermissionsState.allPermissionsGranted}")
//        }
//    }

    val homeDirectory = Environment.getExternalStorageDirectory().path
    var directory by remember { mutableStateOf(homeDirectory) }
    val files by remember {
        derivedStateOf {
            File(directory).listFiles()?.toList() ?: emptyList()
        }
    }

    val onFileSelect: (String) -> Unit = {
        // TODO start importing
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ShowTitle(
            title = stringResource(R.string.choose_file),
            navigateBack = navigateBack
        )

        Spacer(Modifier.width(8.dp))

        LazyColumn {
            items(
                items = files.sortedBy { !it.isDirectory },
                key = { it.path }
            ) { file ->
                if (file.isDirectory) {
                    FolderItem(
                        file = file,
                        onItemClick = {
                            directory = file.path
                        },
                    )
                } else if (file.isFile) {
                    FileItem(
                        file = file,
                        onItemClick = { path ->
                            onFileSelect(path)
                        },
                    )
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
    val dateFormat = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault())
    val creationDate = dateFormat.format(Date(file.lastModified()))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            .clickable { onItemClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .aspectRatio(1f)
            ) {
                Icon(
                    imageVector = folderIcon,
                    contentDescription = stringResource(R.string.folder_explorer_item),
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier,
            ) {
                Text(
                    file.name.take(DEFAULT_NAME_LENGTH * 2)
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

@Composable
private fun FileItem(
    file: File,
    onItemClick: (String) -> Unit,
) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault())
    val creationDate = dateFormat.format(Date(file.lastModified()))
    val icon = when (file.extension.lowercase()) {
        "mp3" -> audioFileIcon
        "mp4" -> videoFileIcon
        "jpg", "png", "jpeg", "gif" -> imageFileIcon
        "xls", "xlsx" -> tablesFileIcon
        "json" -> jsonFileIcon
        "txt", "doc", "docx", "pdf" -> textFileIcon
        else -> fileIcon
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            .clickable { onItemClick(file.path) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .aspectRatio(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = stringResource(R.string.file_explorer_item),
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier,
            ) {
                Text(
                    if (file.name.length > DEFAULT_NAME_LENGTH + 4) {
                        file.name.take(DEFAULT_NAME_LENGTH) + "..." + file.extension
                    } else {
                        file.name
                    }
                )

                Text(
                    stringResource(
                        R.string.file_explorer_item_date_size,
                        creationDate,
                        file.getFormattedFileSize()
                    )
                )

            }

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = stringResource(R.string.file_explorer_item),
                modifier = Modifier,
            )
        }

    }
}

@Composable
private fun NoPermission(
    requestPermission: () -> Unit,
    text: String = stringResource(R.string.provide_permission),
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = requestPermission) {
            Text(
                text = stringResource(R.string.provide),
                style = MaterialTheme.typography.titleMedium
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

@Preview
@Composable
private fun FileItemPreview() {
    FileItem(
        file = File(Environment.getExternalStorageDirectory().path + "/sample.mp3"),
        onItemClick = { },
    )
}
