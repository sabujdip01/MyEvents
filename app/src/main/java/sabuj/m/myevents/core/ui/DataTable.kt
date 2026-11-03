package sabuj.m.myevents.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelM

/**
 * Generic data table with a header row and data rows.
 *
 * @param headers  list of column header strings.
 * @param rows     list of rows; each row is a list of cell strings (must match header count).
 */
@Composable
fun DataTable(
    headers: List<String>,
    rows: List<List<String>>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.card))
            .background(AppColor.surface),
    ) {
        // Header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColor.primaryContainer)
                .padding(horizontal = Spacing.md, vertical = Spacing.sm)
                .horizontalScroll(rememberScrollState()),
        ) {
            headers.forEach { header ->
                Text(
                    text      = header,
                    style     = labelM,
                    color     = AppColor.onPrimaryContainer,
                    modifier  = Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                )
            }
        }
        HorizontalDivider(color = AppColor.outlineVariant, thickness = 0.5.dp)
        // Data rows
        rows.forEachIndexed { rowIndex, cells ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (rowIndex % 2 == 0) AppColor.surface else AppColor.surfaceVariant)
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm)
                    .horizontalScroll(rememberScrollState()),
            ) {
                cells.forEach { cell ->
                    Text(
                        text     = cell,
                        style    = bodyM,
                        color    = AppColor.text,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            if (rowIndex < rows.lastIndex) {
                HorizontalDivider(color = AppColor.outlineVariant, thickness = 0.5.dp)
            }
        }
    }
}

@LightDarkPreviews
@Composable
private fun DataTablePreview() {
    AppTheme {
        DataTable(
            headers  = listOf("Name", "Amount", "Status"),
            rows     = listOf(
                listOf("Catering", "₹15,000", "Paid"),
                listOf("Décor",    "₹8,000",  "Pending"),
            ),
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}

