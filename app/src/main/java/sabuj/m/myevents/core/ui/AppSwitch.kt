package sabuj.m.myevents.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
 * App-styled toggle switch with an optional leading label.
 * Colours follow the active palette.
 */
@Composable
fun AppSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    enabled: Boolean = true,
) {
    Row(
        modifier              = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        if (label.isNotEmpty()) {
            Text(
                text     = label,
                style    = bodyM,
                color    = AppColor.text,
                modifier = Modifier.weight(1f),
            )
        }
        Switch(
            checked         = checked,
            onCheckedChange = onCheckedChange,
            enabled         = enabled,
            colors          = SwitchDefaults.colors(
                checkedTrackColor   = AppColor.primary,
                checkedThumbColor   = AppColor.onPrimary,
                uncheckedTrackColor = AppColor.surfaceVariant,
                uncheckedThumbColor = AppColor.text3,
            ),
        )
    }
}

@LightDarkPreviews
@Composable
private fun AppSwitchPreview() {
    AppTheme {
        var on by remember { mutableStateOf(true) }
        AppSwitch(
            checked         = on,
            onCheckedChange = { on = it },
            label           = "Enable sync",
            modifier        = Modifier.padding(Spacing.lg),
        )
    }
}

