package sabuj.m.myevents.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.labelL

/**
 * App-wide button variants — design.md section 4.5.
 * Height 52, r-field 14, label-l text.
 */

/** Filled primary button (`.btn--primary`). */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
) {
    Button(
        onClick   = onClick,
        modifier  = modifier.height(52.dp).widthIn(min = 96.dp),
        enabled   = enabled,
        shape     = RoundedCornerShape(Radius.field),
        colors    = ButtonDefaults.buttonColors(
            containerColor         = AppColor.primary,
            contentColor           = AppColor.onPrimary,
            disabledContainerColor = AppColor.surfaceVariant,
            disabledContentColor   = AppColor.text3,
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
    ) {
        if (leadingIcon != null) {
            Icon(leadingIcon, null, modifier = Modifier.size(18.dp))
            androidx.compose.foundation.layout.Spacer(Modifier.size(8.dp))
        }
        Text(text, style = labelL)
    }
}

/** Tonal button (`.btn--tonal`): primaryContainer fill. */
@Composable
fun AppButtonTonal(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
) {
    Button(
        onClick   = onClick,
        modifier  = modifier.height(52.dp).widthIn(min = 96.dp),
        enabled   = enabled,
        shape     = RoundedCornerShape(Radius.field),
        colors    = ButtonDefaults.buttonColors(
            containerColor         = AppColor.primaryContainer,
            contentColor           = AppColor.onPrimaryContainer,
            disabledContainerColor = AppColor.surfaceVariant,
            disabledContentColor   = AppColor.text3,
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
    ) {
        if (leadingIcon != null) {
            Icon(leadingIcon, null, modifier = Modifier.size(18.dp))
            androidx.compose.foundation.layout.Spacer(Modifier.size(8.dp))
        }
        Text(text, style = labelL)
    }
}

/** Danger button (`.btn--danger`): dangerContainer fill. */
@Composable
fun AppButtonDanger(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
) {
    Button(
        onClick   = onClick,
        modifier  = modifier.height(52.dp).widthIn(min = 96.dp),
        enabled   = enabled,
        shape     = RoundedCornerShape(Radius.field),
        colors    = ButtonDefaults.buttonColors(
            containerColor = AppColor.dangerContainer,
            contentColor   = AppColor.danger,
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
    ) {
        if (leadingIcon != null) {
            Icon(leadingIcon, null, modifier = Modifier.size(18.dp))
            androidx.compose.foundation.layout.Spacer(Modifier.size(8.dp))
        }
        Text(text, style = labelL)
    }
}

/** Text-only button (`.btn--text`). */
@Composable
fun AppButtonText(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    TextButton(
        onClick  = onClick,
        modifier = modifier.height(52.dp),
        enabled  = enabled,
        shape    = RoundedCornerShape(Radius.field),
        colors   = ButtonDefaults.textButtonColors(contentColor = AppColor.primary),
    ) {
        Text(text, style = labelL)
    }
}

/**
 * Two equal-width buttons side by side — `.btn-row`.
 * Default: Cancel (tonal) on the left, Save/primary on the right.
 */
@Composable
fun AppButtonRow(
    positiveText: String,
    onPositive: () -> Unit,
    negativeText: String,
    onNegative: () -> Unit,
    modifier: Modifier = Modifier,
    positiveEnabled: Boolean = true,
    isDangerous: Boolean = false,
) {
    Row(
        modifier            = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        AppButtonTonal(
            text     = negativeText,
            onClick  = onNegative,
            modifier = Modifier.weight(1f),
        )
        if (isDangerous) {
            AppButtonDanger(
                text     = positiveText,
                onClick  = onPositive,
                modifier = Modifier.weight(1f),
                enabled  = positiveEnabled,
            )
        } else {
            AppButton(
                text     = positiveText,
                onClick  = onPositive,
                modifier = Modifier.weight(1f),
                enabled  = positiveEnabled,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@LightDarkPreviews
@Composable
private fun AppButtonPreview() {
    AppTheme {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            AppButton("Save", onClick = {}, modifier = Modifier.fillMaxWidth())
            AppButtonTonal("Cancel", onClick = {}, modifier = Modifier.fillMaxWidth())
            AppButtonDanger("Delete", onClick = {}, modifier = Modifier.fillMaxWidth())
            AppButtonRow("Save", {}, "Cancel", {})
        }
    }
}
