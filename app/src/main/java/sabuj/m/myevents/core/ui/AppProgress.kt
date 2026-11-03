package sabuj.m.myevents.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.labelM

/**
 * Labelled progress bar for budget/payment tracking.
 * Shows label on the left and a percentage or value string on the right.
 *
 * @param progress  fraction (0.0–1.0) of progress.
 * @param label     description shown to the left of the bar.
 * @param trailing  optional string shown to the right (e.g. "₹12,000 / ₹20,000").
 */
@Composable
fun AppProgress(
    progress: Float,
    label: String,
    modifier: Modifier = Modifier,
    trailing: String = "${(progress * 100).toInt()}%",
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Text(label, style = labelM, color = AppColor.text2)
            Text(trailing, style = labelM, color = AppColor.text2)
        }
        LinearProgressIndicator(
            progress         = { progress.coerceIn(0f, 1f) },
            modifier         = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.xs)
                .height(6.dp)
                .clip(RoundedCornerShape(Radius.pill)),
            color            = if (progress >= 0.9f) AppColor.danger else AppColor.primary,
            trackColor       = AppColor.surfaceVariant,
        )
    }
}

@LightDarkPreviews
@Composable
private fun AppProgressPreview() {
    AppTheme {
        Column(modifier = Modifier.padding(Spacing.lg), verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
            AppProgress(progress = 0.4f, label = "Budget used")
            AppProgress(progress = 0.92f, label = "Budget used", trailing = "₹18,400 / ₹20,000")
        }
    }
}

