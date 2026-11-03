package sabuj.m.myevents.core.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.labelL

/**
 * App-wide tab row. All screens that need tabs use this component
 * for consistent indicator colour and typography.
 *
 * @param tabs     list of tab labels.
 * @param selected index of the currently selected tab.
 * @param onSelect callback with the new tab index.
 */
@Composable
fun AppTabs(
    tabs: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    TabRow(
        selectedTabIndex = selected,
        modifier         = modifier.fillMaxWidth(),
        containerColor   = AppColor.background,
        contentColor     = AppColor.primary,
        indicator        = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier  = Modifier.tabIndicatorOffset(tabPositions[selected]),
                color     = AppColor.primary,
            )
        },
    ) {
        tabs.forEachIndexed { index, label ->
            Tab(
                selected         = index == selected,
                onClick          = { onSelect(index) },
                text             = {
                    Text(
                        text  = label,
                        style = labelL,
                        color = if (index == selected) AppColor.primary else AppColor.text2,
                    )
                },
            )
        }
    }
}

@LightDarkPreviews
@Composable
private fun AppTabsPreview() {
    AppTheme {
        var sel by remember { mutableStateOf(0) }
        AppTabs(
            tabs     = listOf("All", "Upcoming", "Past"),
            selected = sel,
            onSelect = { sel = it },
            modifier = Modifier.padding(vertical = Spacing.sm),
        )
    }
}

