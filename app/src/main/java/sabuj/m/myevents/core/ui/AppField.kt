package sabuj.m.myevents.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelM

/**
 * Standard outlined text field with label, optional error text,
 * and optional trailing icon. Used in all add/edit sheets.
 */
@Composable
fun AppField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    errorText: String? = null,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value           = value,
            onValueChange   = onValueChange,
            label           = { Text(label, style = labelM) },
            placeholder     = if (placeholder.isNotEmpty()) ({ Text(placeholder, style = bodyM) }) else null,
            isError         = isError,
            singleLine      = singleLine,
            maxLines        = maxLines,
            enabled         = enabled,
            trailingIcon    = trailingIcon,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            shape           = RoundedCornerShape(Radius.field),
            colors          = OutlinedTextFieldDefaults.colors(
                focusedBorderColor    = AppColor.primary,
                unfocusedBorderColor  = AppColor.outline,
                errorBorderColor      = AppColor.danger,
                focusedLabelColor     = AppColor.primary,
                unfocusedLabelColor   = AppColor.text2,
                cursorColor           = AppColor.primary,
            ),
            modifier        = Modifier.fillMaxWidth(),
        )
        if (isError && errorText != null) {
            Text(
                text     = errorText,
                style    = labelM,
                color    = AppColor.danger,
                modifier = Modifier.padding(start = Spacing.sm, top = Spacing.xs),
            )
        }
    }
}

@LightDarkPreviews
@Composable
private fun AppFieldPreview() {
    AppTheme {
        var text by remember { mutableStateOf("") }
        Column(modifier = Modifier.padding(Spacing.lg)) {
            AppField(value = text, onValueChange = { text = it }, label = "Event name")
            AppField(value = "Bad input", onValueChange = {}, label = "Amount", isError = true, errorText = "Required")
        }
    }
}

