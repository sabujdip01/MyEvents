package sabuj.m.myevents.core.ui

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.labelM
import sabuj.m.myevents.core.util.MoneyFormatter

/**
 * Specialised field for rupee/paise money entry.
 * Shows prefix "₹" and accepts a Long value in paise internally.
 * Displays the value converted to a rupee integer string.
 */
@Composable
fun MoneyField(
    valuePaise: Long,
    onValueChange: (Long) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorText: String? = null,
    imeAction: ImeAction = ImeAction.Next,
) {
    val text = remember(valuePaise) {
        if (valuePaise == 0L) "" else (valuePaise / 100L).toString()
    }
    Column(modifier = modifier) {
        OutlinedTextField(
            value         = text,
            onValueChange = { raw ->
                val cleaned = raw.filter { it.isDigit() }
                val rupees  = cleaned.toLongOrNull() ?: 0L
                onValueChange(rupees * 100L)
            },
            label           = { Text(label, style = labelM) },
            prefix          = { Text("₹", style = labelM, color = AppColor.text2) },
            singleLine      = true,
            isError         = isError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = imeAction),
            shape           = RoundedCornerShape(Radius.field),
            colors          = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = AppColor.primary,
                unfocusedBorderColor = AppColor.outline,
                errorBorderColor     = AppColor.danger,
                focusedLabelColor    = AppColor.primary,
                unfocusedLabelColor  = AppColor.text2,
                cursorColor          = AppColor.primary,
            ),
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
private fun MoneyFieldPreview() {
    AppTheme {
        var paise by remember { mutableStateOf(150000L) }
        MoneyField(
            valuePaise    = paise,
            onValueChange = { paise = it },
            label         = "Budget",
            modifier      = Modifier.padding(Spacing.lg),
        )
    }
}

