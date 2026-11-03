package sabuj.m.myevents.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.labelL

/**
 * Two- or three-segment control for mutually-exclusive option selection.
 * Equivalent to a radio button group rendered as a pill bar.
 *
 * @param options   list of label strings to display as segments.
 * @param selected  index of the currently selected segment.
 * @param onSelect  callback with the newly selected index.
 */
@Composable
fun Segmented(
    options: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape  = RoundedCornerShape(Radius.field),
        color  = AppColor.surfaceVariant,
        modifier = modifier.height(44.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            options.forEachIndexed { index, label ->
                val isSelected = index == selected
                Surface(
                    onClick  = { onSelect(index) },
                    shape    = RoundedCornerShape(Radius.field),
                    color    = if (isSelected) AppColor.primary else AppColor.surfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .height(36.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment     = Alignment.CenterVertically,
                    ) {
                        Text(
                            text  = label,
                            style = labelL,
                            color = if (isSelected) AppColor.onPrimary else AppColor.text2,
                        )
                    }
                }
            }
        }
    }
}

@LightDarkPreviews
@Composable
private fun SegmentedPreview() {
    AppTheme {
        var sel by remember { mutableStateOf(0) }
        Segmented(
            options  = listOf("All", "Active", "Done"),
            selected = sel,
            onSelect = { sel = it },
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}

