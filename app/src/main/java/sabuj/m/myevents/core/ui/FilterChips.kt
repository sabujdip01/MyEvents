package sabuj.m.myevents.core.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.labelL

/**
 * Horizontally scrollable row of filter chips for list screens.
 *
 * @param options  list of filter label strings.
 * @param selected index of the currently active filter.
 * @param onSelect callback with the newly selected index.
 */
@Composable
fun FilterChips(
    options: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier              = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        options.forEachIndexed { index, label ->
            val isSelected = index == selected
            FilterChip(
                selected  = isSelected,
                onClick   = { onSelect(index) },
                label     = { Text(label, style = labelL) },
                shape     = RoundedCornerShape(Radius.chip),
                colors    = FilterChipDefaults.filterChipColors(
                    containerColor         = AppColor.surfaceVariant,
                    labelColor             = AppColor.text2,
                    selectedContainerColor = AppColor.primaryContainer,
                    selectedLabelColor     = AppColor.onPrimaryContainer,
                ),
                border    = FilterChipDefaults.filterChipBorder(
                    enabled               = true,
                    selected              = isSelected,
                    borderColor           = AppColor.outlineVariant,
                    selectedBorderColor   = AppColor.primary,
                    borderWidth           = if (isSelected) Spacing.xs / 4 else Spacing.xs / 4,
                    selectedBorderWidth   = Spacing.xs / 4,
                ),
            )
        }
    }
}

@LightDarkPreviews
@Composable
private fun FilterChipsPreview() {
    AppTheme {
        var sel by remember { mutableStateOf(0) }
        FilterChips(
            options  = listOf("All", "Upcoming", "Past"),
            selected = sel,
            onSelect = { sel = it },
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}

