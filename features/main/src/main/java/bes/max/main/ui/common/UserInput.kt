package bes.max.main.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UserInput(
    @StringRes hintRes: Int,
    initialText: String = "",
    onValueChanged: ((String) -> Unit)? = null
) {
    var text by remember { mutableStateOf(initialText) }
    TextField(
        value = text,
        onValueChange = {
            text = it
            onValueChanged?.invoke(it)
        },
        label = { Text(text = stringResource(id = hintRes)) },
        maxLines = 1,
        textStyle = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = LightGray,
            unfocusedContainerColor = LightGray,
            disabledContainerColor = LightGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .onFocusChanged {
                if (it.hasFocus && text.isBlank()) {
                    onValueChanged?.invoke(text)
                }
            }
    )
}