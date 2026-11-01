package sabuj.m.myevents.feature.events

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelL
import sabuj.m.myevents.core.theme.labelM
import sabuj.m.myevents.core.theme.numberXl
import sabuj.m.myevents.core.theme.titleL
import sabuj.m.myevents.core.theme.titleS
import sabuj.m.myevents.core.ui.IconChip
import sabuj.m.myevents.core.ui.IconChipSize
import sabuj.m.myevents.core.util.DateUtils
import sabuj.m.myevents.core.util.MoneyFormatter
import sabuj.m.myevents.domain.model.Event

/**
 * Bottom sheet showing event details.
 * Displays invitees/bookings stats tiles, a detail list, and Edit/Delete buttons.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsSheet(
    event: Event,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest    = onDismiss,
        sheetState          = sheetState,
        containerColor      = AppColor.surface,
        shape               = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        ) {
            // ── Header: icon + title + date ─────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconChip(
                    icon               = event.icon.fallback,
                    contentDescription = event.title,
                    size               = IconChipSize.Lg,
                )
                Spacer(Modifier.width(Spacing.md))
                Column {
                    Text(
                        text  = event.title,
                        style = titleL,
                        color = AppColor.text,
                    )
                    Text(
                        text  = DateUtils.formatDisplay(event.date),
                        style = bodyM,
                        color = AppColor.text2,
                    )
                }
            }

            Spacer(Modifier.height(Spacing.lg))

            // ── Stat tiles ──────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                StatTile(
                    label   = "Invitees",
                    value   = if (event.inviteesRequired) "0" else "—",
                    caption = if (event.inviteesRequired) "people" else "Not required",
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    label   = "Bookings",
                    value   = "0",
                    caption = "bookings",
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(Spacing.lg))

            // ── Detail list ─────────────────────────────────────────
            DetailRow(label = "Event Name", value = event.eventName)
            HorizontalDivider(color = AppColor.outlineVariant, thickness = 0.5.dp)
            DetailRow(label = "Date", value = DateUtils.formatDisplay(event.date))
            HorizontalDivider(color = AppColor.outlineVariant, thickness = 0.5.dp)
            DetailRow(label = "Budget", value = MoneyFormatter.format(event.budgetPaise))
            HorizontalDivider(color = AppColor.outlineVariant, thickness = 0.5.dp)
            DetailRow(
                label = "Invitees Required",
                value = if (event.inviteesRequired) "Yes" else "No",
            )

            Spacer(Modifier.height(Spacing.xl))

            // ── Action buttons ──────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Button(
                    onClick  = onEdit,
                    modifier = Modifier.weight(1f),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = AppColor.primaryContainer,
                        contentColor   = AppColor.onPrimaryContainer,
                    ),
                    shape    = RoundedCornerShape(14.dp),
                ) {
                    Icon(Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Edit Event", style = labelL)
                }
                Button(
                    onClick  = onDelete,
                    modifier = Modifier.weight(1f),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = AppColor.danger.copy(alpha = 0.12f),
                        contentColor   = AppColor.danger,
                    ),
                    shape    = RoundedCornerShape(14.dp),
                ) {
                    Icon(Icons.Rounded.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Delete", style = labelL)
                }
            }

            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

@Composable
private fun StatTile(
    label: String,
    value: String,
    caption: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = modifier
            .padding(Spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = label, style = labelM, color = AppColor.text2)
        Spacer(Modifier.height(4.dp))
        Text(text = value, style = numberXl, color = AppColor.text)
        Text(text = caption, style = bodyM, color = AppColor.text2)
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(horizontal = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text     = label,
            style    = bodyM,
            color    = AppColor.text2,
            modifier = Modifier.weight(1f),
        )
        Text(
            text  = value,
            style = titleS,
            color = AppColor.text,
        )
    }
}
