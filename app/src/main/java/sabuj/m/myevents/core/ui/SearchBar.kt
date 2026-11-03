package sabuj.m.myevents.core.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelM

/**
 * App-wide search field. Renders as an outlined text field with a leading
 * search icon and placeholder text.
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search…",
) {
    OutlinedTextField(
        value           = query,
        onValueChange   = onQueryChange,
        placeholder     = { Text(placeholder, style = bodyM, color = AppColor.text3) },
        leadingIcon     = { Icon(Icons.Rounded.Search, contentDescription = null, tint = AppColor.text2) },
        singleLine      = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        shape           = RoundedCornerShape(Radius.pill),
        colors          = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = AppColor.primary,
            unfocusedBorderColor = AppColor.outlineVariant,
            cursorColor          = AppColor.primary,
        ),
        modifier = modifier.fillMaxWidth(),
    )
}

@LightDarkPreviews
@Composable
private fun SearchBarPreview() {
    AppTheme {
        var q by remember { mutableStateOf("") }
        SearchBar(
            query         = q,
            onQueryChange = { q = it },
            modifier      = Modifier.padding(Spacing.lg),
        )
    }
}

