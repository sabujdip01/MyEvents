package sabuj.m.myevents.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM

/**
 * App-styled checkbox with an optional inline label.
 * Colours follow the active palette so it matches the rest of the UI.
 */
@Composable
fun AppCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    enabled: Boolean = true,
) {
    Row(
        modifier              = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked         = checked,
            onCheckedChange = onCheckedChange,
            enabled         = enabled,
            colors          = CheckboxDefaults.colors(
                checkedColor   = AppColor.primary,
                checkmarkColor = AppColor.onPrimary,
            ),
        )
        if (label.isNotEmpty()) {
            Text(text = label, style = bodyM, color = AppColor.text)
        }
    }
}

@LightDarkPreviews
@Composable
private fun AppCheckboxPreview() {
    AppTheme {
        var checked by remember { mutableStateOf(true) }
        AppCheckbox(
            checked         = checked,
            onCheckedChange = { checked = it },
            label           = "Mark as done",
            modifier        = Modifier.padding(Spacing.lg),
        )
    }
}

