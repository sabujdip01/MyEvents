package sabuj.m.myevents.feature.events

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Label
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.icons.AppIcon
import sabuj.m.myevents.core.icons.EventIconDisplay
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.preview.PreviewData
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.ui.AppButtonDanger
import sabuj.m.myevents.core.ui.AppButtonTonal
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelL
import sabuj.m.myevents.core.theme.labelM
import sabuj.m.myevents.core.theme.numberL
import sabuj.m.myevents.core.theme.titleL
import sabuj.m.myevents.core.theme.titleS
import sabuj.m.myevents.core.util.DateUtils
import sabuj.m.myevents.core.util.MoneyFormatter
import sabuj.m.myevents.domain.model.Event

/**
 * Bottom sheet showing event details — matches design.md Event screen sample.
 * Stat tiles (Invitees, Bookings) each have an icon.
 * Detail rows have a small icon prefix matching the field type.
 * Buttons: "Edit Event" (primary container) + "Delete Event" (danger tint).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsSheet(
    event: Event,
    bookingCount: Int = 0,
    inviteeCount: Int = 0,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    EventDetailsSheet(
        event        = event,
        bookingCount = bookingCount,
        inviteeCount = inviteeCount,
        showActions  = true,
        onDismiss    = onDismiss,
        onEdit       = onEdit,
        onDelete     = onDelete,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsSheet(
    event: Event,
    bookingCount: Int = 0,
    inviteeCount: Int = 0,
    showActions: Boolean = true,
    onDismiss: () -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange    = { target -> target != SheetValue.Hidden },
    )

    ModalBottomSheet(
        onDismissRequest    = onDismiss,
        sheetState          = sheetState,
        containerColor      = AppColor.surface,
        shape               = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle          = null,
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
    ) {
        EventDetailsContent(
            event        = event,
            bookingCount = bookingCount,
            inviteeCount = inviteeCount,
            showActions  = showActions,
            onEdit       = onEdit,
            onDelete     = onDelete,
            modifier     = Modifier.statusBarsPadding().navigationBarsPadding(),
        )
    }
}

@Composable
fun EventDetailsContent(
    event: Event,
    bookingCount: Int = 0,
    inviteeCount: Int = 0,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    EventDetailsContent(
        event        = event,
        bookingCount = bookingCount,
        inviteeCount = inviteeCount,
        showActions  = true,
        onEdit       = onEdit,
        onDelete     = onDelete,
    )
}

@Composable
fun EventDetailsContent(
    event: Event,
    bookingCount: Int = 0,
    inviteeCount: Int = 0,
    showActions: Boolean = true,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AppColor.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Spacer(Modifier.height(4.dp))

        // ── Header: icon + title + date ─────────────────────────
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(AppColor.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                EventIconDisplay(
                    icon               = event.icon,
                    contentDescription = event.title,
                    modifier           = Modifier.size(36.dp),
                )
            }
            Spacer(Modifier.width(11.dp))
            Column {
                Text(
                    text  = event.title,
                    style = titleL,
                    color = AppColor.text,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = DateUtils.formatDisplay(event.date),
                    style = bodyM,
                    color = AppColor.text2,
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // ── Stat tiles (E4 — Icon on left, label/value on right) ──
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatTile(
                icon      = AppIcon.Bookings.fallback,
                label     = "Booking / Contracts",
                value     = bookingCount.toString(),
                caption   = if (bookingCount == 1) "Contract" else "Contracts",
                modifier  = Modifier.weight(1f),
            )
            StatTile(
                icon    = Icons.Rounded.Group,
                label   = "Guests",
                value   = inviteeCount.toString(),
                caption = if (inviteeCount == 1) "Person" else "People",
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(10.dp))

        // ── Detail list ─────────────────────────────────────────
        DetailRow(
            icon  = Icons.Rounded.Label,
            label = "Event Type",
            value = event.eventName.lowercase().replaceFirstChar { it.uppercase() },
        )
        HorizontalDivider(color = AppColor.outlineVariant, thickness = 0.5.dp)
        DetailRow(
            icon  = Icons.Rounded.CalendarMonth,
            label = "Date",
            value = DateUtils.formatDisplay(event.date),
        )
        HorizontalDivider(color = AppColor.outlineVariant, thickness = 0.5.dp)
        DetailRow(
            icon  = Icons.Rounded.MonetizationOn,
            label = "Budget",
            value = MoneyFormatter.format(event.budgetPaise),
        )
        HorizontalDivider(color = AppColor.outlineVariant, thickness = 0.5.dp)
        DetailRow(
            icon  = Icons.Rounded.Group,
            label = "Invite Guests?",
            value = if (event.inviteesRequired) "Yes" else "No",
        )

        if (showActions) {
            Spacer(Modifier.height(16.dp))

            // ── Action buttons ──────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AppButtonTonal(
                    text        = "Edit Event",
                    onClick     = onEdit,
                    leadingIcon = Icons.Rounded.Edit,
                    modifier    = Modifier.weight(1f),
                )
                AppButtonDanger(
                    text        = "Delete Event",
                    onClick     = onDelete,
                    leadingIcon = Icons.Rounded.Delete,
                    modifier    = Modifier.weight(1f),
                )
            }
        }

        Spacer(Modifier.height(10.dp))
    }
}

/** Stat tile with icon, big number, label and caption — used for Invitees / Bookings. */
@Composable
private fun StatTile(
    icon: ImageVector,
    label: String,
    value: String,
    caption: String,
    modifier: Modifier = Modifier,
    customRes: Int? = null,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AppColor.surfaceVariant.copy(alpha = 0.5f))
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Icon chip on left
            Box(
                modifier        = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(AppColor.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                if (customRes != null) {
                    Image(
                        painter            = painterResource(customRes),
                        contentDescription = null,
                        modifier           = Modifier.size(28.dp),
                        contentScale       = ContentScale.Fit,
                    )
                } else {
                    Icon(icon, contentDescription = null, tint = AppColor.primary, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(Modifier.width(8.dp))
            // Labels on right
            Column {
                Text(
                    text       = label,
                    style      = labelM,
                    fontWeight = FontWeight.Bold,
                    color      = AppColor.text2,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(text = value, style = numberL, color = AppColor.text)
                    Text(text = caption, style = bodyM, color = AppColor.text2)
                }
            }
        }
    }
}

/** Detail row with a leading icon, left label and right value. */
@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = AppColor.primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
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

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@LightDarkPreviews
@Composable
private fun EventDetailsSheetReadOnlyPreview() {
    AppTheme {
        EventDetailsContent(
            event        = PreviewData.sampleEvents.first(),
            bookingCount = 3,
            inviteeCount = 85,
            showActions  = false,
        )
    }
}

@LightDarkPreviews
@Composable
private fun EventDetailsSheetWithActionsPreview() {
    AppTheme {
        EventDetailsContent(
            event        = PreviewData.sampleEvents.first(),
            bookingCount = 3,
            inviteeCount = 85,
            showActions  = true,
        )
    }
}
